package service.sllbackend.service.impl;

import org.springframework.stereotype.Service;
import service.sllbackend.repository.ShiftTemplateRepo;
import service.sllbackend.service.ShiftScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.ShiftAssignment;
import service.sllbackend.entity.ShiftInstance;
import service.sllbackend.entity.ShiftTemplate;
import service.sllbackend.entity.Staff;
import service.sllbackend.repository.AppointmentRepo;
import service.sllbackend.repository.ShiftAssignmentRepo;
import service.sllbackend.repository.ShiftInstanceRepo;
import service.sllbackend.web.dto.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShiftScheduleServiceImpl implements ShiftScheduleService {

    private final ShiftInstanceRepo shiftInstanceRepo;
    private final ShiftAssignmentRepo shiftAssignmentRepo;
    private final ShiftTemplateRepo shiftTemplateRepo;
    private final AppointmentRepo appointmentRepo;

    @Override
    @Transactional
    public List<CalendarDayViewDTO> buildMonthSchedule(YearMonth month) {
        LocalDate firstOfMonth = month.atDay(1);
        LocalDate lastOfMonth = month.atEndOfMonth();

        // Calendar grid from Monday before first day to Sunday after last day
        LocalDate calendarStart = firstOfMonth.with(DayOfWeek.MONDAY);
        LocalDate calendarEnd = lastOfMonth.with(DayOfWeek.SUNDAY);

        // Load shift instances for the whole calendar window
        List<ShiftInstance> shiftInstances =
                shiftInstanceRepo.findByShiftDateBetween(calendarStart, calendarEnd);

        Map<LocalDate, List<ShiftInstance>> shiftsByDate = shiftInstances.stream()
                .collect(Collectors.groupingBy(ShiftInstance::getShiftDate));

        // Load assignments for these shift instances
        List<ShiftAssignment> assignments =
                shiftAssignmentRepo.findByShiftInstanceIn(shiftInstances);

        Map<Integer, Long> assignmentCountByShiftInstanceId = assignments.stream()
                .collect(Collectors.groupingBy(
                        sa -> sa.getShiftInstance().getId(),
                        Collectors.counting()
                ));

        // Load appointments for the same date range
        LocalDateTime startDateTime = calendarStart.atStartOfDay();
        LocalDateTime endDateTime = calendarEnd.plusDays(1).atStartOfDay();

        List<Appointment> appointments =
                appointmentRepo.findByScheduledAtBetween(startDateTime, endDateTime);

        Map<LocalDate, Long> apptCountByDate = appointments.stream()
                .filter(a -> a.getScheduledAt() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getScheduledAt().toLocalDate(),
                        Collectors.counting()
                ));

        // Build result DTOs
        List<CalendarDayViewDTO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (LocalDate date = calendarStart; !date.isAfter(calendarEnd); date = date.plusDays(1)) {
            CalendarDayViewDTO dto = new CalendarDayViewDTO();
            dto.setDate(date);
            dto.setInCurrentMonth(YearMonth.from(date).equals(month));
            dto.setToday(date.equals(today));
            dto.setTotalAppointments(apptCountByDate.getOrDefault(date, 0L).intValue());

            List<ShiftInstance> dayShifts = shiftsByDate.getOrDefault(date, List.of());

            for (ShiftInstance shift : dayShifts) {
                ShiftTemplate template = shift.getShiftTemplate();
                // shift_start is LocalDateTime; we only care about time-of-day
                LocalTime startTime = template.getShiftStart();
                boolean isMorning = startTime.isBefore(LocalTime.NOON);

                int assignedCount = assignmentCountByShiftInstanceId
                        .getOrDefault(shift.getId(), 0L)
                        .intValue();

                String loadLevel = calculateLoadLevel(dto.getTotalAppointments());

                if (isMorning) {
                    dto.setHasMorningShift(true);
                    dto.setAmAssignedStaffCount(assignedCount);
                    dto.setAmRecommendedStaffCount(assignedCount); // later you can change this
                    dto.setAmLoadLevel(loadLevel);
                } else {
                    dto.setHasAfternoonShift(true);
                    dto.setPmAssignedStaffCount(assignedCount);
                    dto.setPmRecommendedStaffCount(assignedCount);
                    dto.setPmLoadLevel(loadLevel);
                }
            }

            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional
    public DayShiftScheduleViewDTO buildDaySchedule(LocalDate date) {
        DayShiftScheduleViewDTO dto = new DayShiftScheduleViewDTO();
        dto.setDate(date);

        // 1) Fetch shift instances + assignments for this date
        List<ShiftInstance> dayShifts = shiftInstanceRepo.findByShiftDate(date);

        Map<Integer, List<ShiftAssignment>> assignmentsByShiftId =
                shiftAssignmentRepo.findByShiftInstanceIn(dayShifts)
                        .stream()
                        .collect(Collectors.groupingBy(sa -> sa.getShiftInstance().getId()));

        // 2) Fetch appointments for this date
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments =
                appointmentRepo.findByScheduledAtBetween(start, end);

        // Map for quick lookup of preferences by staff + shift
        Map<Integer, Integer> preferenceCountByStaffAndShift =
                buildPreferenceCountByStaffAndShift(dayShifts, appointments);

        // 3) Build shift blocks (AM/PM)
        List<ShiftBlockViewDTO> shiftBlocks = new ArrayList<>();

        for (ShiftInstance shiftInstance : dayShifts) {
            ShiftTemplate template = shiftInstance.getShiftTemplate();
            LocalTime startTime = template.getShiftStart();
            LocalTime endTime = template.getShiftEnd();

            boolean isMorning = startTime.isBefore(LocalTime.NOON);
            String shiftName = isMorning ? "AM" : "PM";

            ShiftBlockViewDTO block = new ShiftBlockViewDTO();
            block.setShiftInstanceId(shiftInstance.getId());
            block.setShiftName(shiftName);
            block.setTimeRange(formatTimeRange(startTime, endTime));

            // Assigned staff
            List<ShiftAssignment> shiftAssignments =
                    assignmentsByShiftId.getOrDefault(shiftInstance.getId(), List.of());

            List<StaffAssignmentViewDTO> assignedStaffDTOs = shiftAssignments.stream()
                    .map(ShiftScheduleServiceImpl::mapToStaffAssignmentDTO)
                    .collect(Collectors.toList());

            block.setAssignedStaff(assignedStaffDTOs);

            // Suggestions based on preference
            List<StaffSuggestionViewDTO> suggestions =
                    buildStaffSuggestionsForShift(shiftInstance, shiftAssignments, preferenceCountByStaffAndShift);

            block.setSuggestedStaff(suggestions);

            shiftBlocks.add(block);
        }

        dto.setShifts(shiftBlocks);

        // 4) Build appointment DTOs, and assign them to the appropriate shift if possible
        List<AppointmentDTO> appointmentDTOs =
                appointments.stream()
                        .map(a -> mapToAppointmentDTO(a, dayShifts))
                        .toList();

        dto.setAppointments(appointmentDTOs);

        return dto;
    }

    @Override
    @Transactional
    public void ensureShiftInstancesForMonth(YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        // Load all templates once (you can later filter by active flag, day-of-week, etc.)
        List<ShiftTemplate> templates = shiftTemplateRepo.findAll();
        System.out.println("Ensuring shifts for month " + month + ", templates found: " + templates.size());

        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            for (ShiftTemplate tmpl : templates) {
                boolean exists = shiftInstanceRepo
                        .existsByShiftDateAndShiftTemplate(day, tmpl);

                if (!exists) {
                    ShiftInstance instance = new ShiftInstance();
                    instance.setShiftDate(day);
                    instance.setShiftTemplate(tmpl);
                    shiftInstanceRepo.save(instance);
                    System.out.println("Created instance: " + day + " / template " + tmpl.getId());
                }
            }
        }
    }

    // ==========================
    // Helpers
    // ==========================

    private String calculateLoadLevel(int apptCount) {
        // super simple thresholds for now
        if (apptCount <= 3) return "low";
        if (apptCount <= 8) return "medium";
        return "high";
    }

    private String formatTimeRange(LocalTime start, LocalTime end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        return start.format(fmt) + " - " + end.format(fmt);
    }

    /**
     * Build a map: key = synthetic "shiftId-staffId" hash, value = preference count
     * for that staff in that shift.
     */
    private Map<Integer, Integer> buildPreferenceCountByStaffAndShift(List<ShiftInstance> dayShifts,
                                                                      List<Appointment> appointments) {

        // Index shifts by time window
        Map<Integer, ShiftInstance> shiftById = dayShifts.stream()
                .collect(Collectors.toMap(ShiftInstance::getId, s -> s));

        Map<Integer, Integer> result = new HashMap<>();

        for (Appointment appt : appointments) {
            if (appt.getScheduledAt() == null || appt.getPreferredStaffId() == null) {
                continue;
            }
            LocalTime time = appt.getScheduledAt().toLocalTime();
            // find which shift this appointment falls into
            ShiftInstance matchedShift = findShiftForTime(dayShifts, time);
            if (matchedShift == null) continue;

            Integer staffId = appt.getPreferredStaffId().getId();
            if (staffId == null) continue;

            int key = Objects.hash(matchedShift.getId(), staffId);
            result.put(key, result.getOrDefault(key, 0) + 1);
        }

        return result;
    }

    private List<StaffSuggestionViewDTO> buildStaffSuggestionsForShift(
            ShiftInstance shiftInstance,
            List<ShiftAssignment> shiftAssignments,
            Map<Integer, Integer> preferenceCountByStaffAndShift
    ) {
        List<StaffSuggestionViewDTO> suggestions = new ArrayList<>();

        for (ShiftAssignment sa : shiftAssignments) {
            Staff staff = sa.getAssignedStaff();
            if (staff == null || staff.getId() == null) {
                continue;
            }

            // Build the same hash key you used when counting preferences:
            // key = hash(shiftId, staffId)
            int key = Objects.hash(shiftInstance.getId(), staff.getId());
            int prefCount = preferenceCountByStaffAndShift.getOrDefault(key, 0);

            StaffSuggestionViewDTO dto = new StaffSuggestionViewDTO();
            dto.setStaffId(staff.getId());
            dto.setStaffName(staff.getName());
            dto.setPreferenceCount(prefCount);
            dto.setCurrentlyAssigned(true);

            suggestions.add(dto);
        }

        // Sort: highest preferenceCount first, then by staffName
        suggestions.sort(
                Comparator.comparingInt(StaffSuggestionViewDTO::getPreferenceCount)
                        .reversed()
                        .thenComparing(
                                StaffSuggestionViewDTO::getStaffName,
                                Comparator.nullsLast(String::compareToIgnoreCase)
                        )
        );

        return suggestions;
    }


    private static StaffAssignmentViewDTO mapToStaffAssignmentDTO(ShiftAssignment assignment) {
        Staff staff = assignment.getAssignedStaff();
        StaffAssignmentViewDTO dto = new StaffAssignmentViewDTO();
        dto.setStaffId(staff.getId());
        dto.setStaffName(staff.getName());
        return dto;
    }

    private AppointmentDTO mapToAppointmentDTO(Appointment appt, List<ShiftInstance> dayShifts) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setAppointmentId(appt.getId());
        dto.setScheduledAt(appt.getScheduledAt());

        if (appt.getPreferredStaffId() != null) {
            dto.setPreferredStaffId(appt.getPreferredStaffId().getId());
            dto.setPreferredStaffName(appt.getPreferredStaffId().getName());
        }

        if (appt.getResponsibleStaffId() != null) {
            dto.setResponsibleStaffId(appt.getResponsibleStaffId().getId());
            dto.setResponsibleStaffName(appt.getResponsibleStaffId().getName());
        }

        // assign shiftInstanceId based on time-of-day
        if (appt.getScheduledAt() != null) {
            LocalTime time = appt.getScheduledAt().toLocalTime();
            ShiftInstance matchedShift = findShiftForTime(dayShifts, time);
            if (matchedShift != null) {
                dto.setShiftInstanceId(matchedShift.getId());
            }
        }

        return dto;
    }

    private ShiftInstance findShiftForTime(List<ShiftInstance> dayShifts, LocalTime time) {
        for (ShiftInstance shiftInstance : dayShifts) {
            ShiftTemplate tmpl = shiftInstance.getShiftTemplate();
            LocalTime start = tmpl.getShiftStart();
            LocalTime end = tmpl.getShiftEnd();

            // inclusive start, exclusive end
            if ((time.equals(start) || time.isAfter(start)) && time.isBefore(end)) {
                return shiftInstance;
            }
        }
        return null;
    }
}
