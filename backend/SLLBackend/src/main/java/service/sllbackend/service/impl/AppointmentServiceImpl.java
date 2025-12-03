package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.enumerator.StaffStatus;
import service.sllbackend.repository.*;
import service.sllbackend.service.AppointmentService;
import service.sllbackend.web.dto.AppointmentRegisterDTO;
import service.sllbackend.web.dto.AvailableStaffDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepo appointmentRepo;
    private final AppointmentDetailsRepo appointmentDetailsRepo;
    private final RequestedServiceRepo requestedServiceRepo;
    private final RequestedServicesServiceImpl requestedServicesService;
    private final UserAccountRepo userAccountRepo;
    private final StaffRepo staffRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getByIdAndStatus(int id, List<AppointmentStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return appointmentRepo.findByUserIdIgnoreCaseContaining(id);
        } else {
            return appointmentRepo.findByUserIdIgnoreCaseContainingAndStatusIn(
                    id, statuses
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getByNameAndStatus(String name, List<AppointmentStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return appointmentRepo.findByNameIgnoreCaseContaining(name != null ? name : "");
        } else {
            return appointmentRepo.findByNameIgnoreCaseContainingAndStatusIn(
                    name != null ? name : "", statuses
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment findById(Long id) {
        return appointmentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
    }

    @Override
    public List<RequestedService> getRequestedServices(Integer appointmentId) {
        return requestedServiceRepo.findByAppointmentId(appointmentId);
    }

    @Override
    public AppointmentDetails getDetailsByAppointmentId(Integer appointmentId) {
        return appointmentDetailsRepo.findByAppointmentId(Long.valueOf(appointmentId)).orElseThrow(() -> new RuntimeException("Can't find details"));
    }

    @Override
    @Transactional
    public void save(Appointment appointment) {
        appointmentRepo.save(appointment);
    }

    @Override
    @Transactional
    public void register(AppointmentRegisterDTO appointmentRegisterDTO) {
        Appointment.AppointmentBuilder builder = Appointment.builder()
                        .registeredAt(LocalDateTime.of(appointmentRegisterDTO.getAppointmentDate(), appointmentRegisterDTO.getAppointmentTime()))
                        .name(appointmentRegisterDTO.getName())
                        .phoneNumber(appointmentRegisterDTO.getPhoneNumber());
        if (appointmentRegisterDTO.getStaffId() != null) {
            staffRepo.findById(appointmentRegisterDTO.getStaffId())
                    .ifPresent(builder::preferredStaffId);
        }

        Appointment savedAppointment = appointmentRepo.save(builder.build());

        List<Integer> serviceIds = Arrays.stream(appointmentRegisterDTO.getSelectedServices().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::valueOf)
                .toList();

        requestedServicesService.flattenAndSave(serviceIds, savedAppointment.getId());
        AppointmentDetails details = new AppointmentDetails();
        details.setAppointment(savedAppointment);
        userAccountRepo
                .findByUsernameIgnoreCase(appointmentRegisterDTO.getName()).ifPresent(details::setUser);
        appointmentDetailsRepo.save(details);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableStaffDTO> findAvailableStaff(LocalDate date,
                                                      LocalTime startTime,
                                                      int durationMinutes) {

        if (date == null || startTime == null || durationMinutes <= 0) {
            return Collections.emptyList();
        }

        LocalDateTime requestedStart = LocalDateTime.of(date, startTime);
        LocalDateTime requestedEnd = requestedStart.plusMinutes(durationMinutes);

        List<Staff> activeStaff = staffRepo.findByStaffStatus(StaffStatus.ACTIVE);
        if (activeStaff.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> staffIds = activeStaff.stream()
                .map(Staff::getId)
                .toList();


        List<Object[]> rawCounts = appointmentDetailsRepo
                .findStaffBusyCountsInInterval(staffIds, requestedStart, requestedEnd);

        Map<Integer, Long> busyCountMap = new HashMap<>();
        for (Object[] row : rawCounts) {
            Integer staffId = (Integer) row[0];
            Long count = (Long) row[1];
            busyCountMap.put(staffId, count);
        }

        Set<Integer> busyStaffIds = busyCountMap.entrySet().stream()
                .filter(e -> e.getValue() >= 3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        return activeStaff.stream()
                .filter(staff -> !busyStaffIds.contains(staff.getId()))
                .map(staff -> AvailableStaffDTO.builder()
                        .id(staff.getId())
                        .name(staff.getName())
                        .build())
                .toList();
    }
}
