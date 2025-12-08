package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.service.*;
import service.sllbackend.utils.DTOMapper;
import service.sllbackend.web.dto.AppointmentDetailsEditDTO;
import service.sllbackend.web.dto.AppointmentDetailsViewDTO;
import service.sllbackend.web.dto.RequestedServiceEditDTO;
import service.sllbackend.web.dto.SimpleUserDTO;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/registration")
@RequiredArgsConstructor
@Slf4j
public class ManagerRegistrationManagementController {
    private final AppointmentService appointmentService;
    private final AppointmentDetailsService appointmentDetailsService;
    private final UserAccountService userAccountService;
    private final RequestedServicesService requestedServicesService;
    private final StaffAccountService staffAccountService;
    private final StaffService staffService;
    private final ServicesService servicesService;
    private final DTOMapper dtoMapper;

    @GetMapping("/list")
    public String staffRegistrationList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<AppointmentStatus> statuses,
            Model model) {

        List<Appointment> registrations;
        if (name == null) {
             registrations = appointmentService.getByNameAndStatus(name, statuses);
        } else {
            registrations = appointmentService.getByPhoneNumberAndStatus(name, statuses);
        }

        model.addAttribute("registrations", registrations);
        model.addAttribute("allStatuses", AppointmentStatus.values());

        return "manager-registration-list";
    }

    @GetMapping("/view/{id}")
    public String staffRegistrationView(@PathVariable int id, Model model) {
        Appointment appointment = appointmentService.findById((long) id);

        AppointmentDetails appointmentDetails = appointmentDetailsService.findByAppointmentId(id);
        AppointmentDetailsViewDTO appointmentDetailsDTO;
        boolean disableDetails = false;

        if (appointmentDetails != null && appointmentDetails.getScheduledStart() != null && appointment.getResponsibleStaffId() != null) {
            UserAccount user = appointmentDetails.getUser();
            appointmentDetailsDTO = dtoMapper.toAppointmentDetailsViewDTO(appointmentDetails, user);
        } else {
            appointmentDetailsDTO = new AppointmentDetailsViewDTO();
            disableDetails = true;
        }

        List<RequestedService> requestedServices = requestedServicesService.findByAppointmentId(id);

        List<StaffAccount> allStaff = staffAccountService.findAllActive();

        model.addAttribute("allStatuses", AppointmentStatus.values());

        model.addAttribute("appointment", appointment);
        model.addAttribute("appointmentDetails", appointmentDetailsDTO);
        model.addAttribute("disableDetails", disableDetails);
        model.addAttribute("requestedServices", requestedServices);
        model.addAttribute("allStaff", allStaff);

        return "manager-registration-edit";
    }


    @PostMapping("/save/{id}")
    public String staffRegistrationSave(@PathVariable int id,
                                        @Valid @ModelAttribute AppointmentDetailsEditDTO dto,
                                        BindingResult bindingResult,
                                        Model model) {
        if (bindingResult.hasErrors()) {
            log.error(bindingResult.getAllErrors().toString());
            return "redirect:/manager/registration/view/" + id;
        }

        // ===== 1. Update Appointment fields =====
        Appointment appointment = appointmentService.findById((long) id);

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            appointment.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getScheduledAt() != null) {
            appointment.setScheduledAt(dto.getScheduledAt());
        }

        if (dto.getStatus() != null) {
            appointment.setStatus(dto.getStatus());
        }

        if (dto.getAssignedStaffId() != null) {
            appointment.setResponsibleStaffId(staffService.findById(dto.getAssignedStaffId()));
        }

        appointmentService.save(appointment);

        // ===== 2. Update AppointmentDetails fields =====
        AppointmentDetails details = appointmentDetailsService.findByAppointmentId(id);

        if (dto.getActualStart() != null) {
            details.setActualStart(dto.getActualStart());
        }

        if (dto.getActualEnd() != null) {
            details.setActualEnd(dto.getActualEnd());
        }

        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            UserAccount user = userAccountService.findByUsername(dto.getUsername());
            details.setUser(user);
        }

        appointmentDetailsService.save(details);

        // ===== 3. Sync requested services (add / update / delete) =====
        List<RequestedService> existing = requestedServicesService.findByAppointmentId(id);

// Map existing RS by their Integer ID (because getId() returns int)
        Map<Integer, RequestedService> existingById = existing.stream()
                .collect(Collectors.toMap(RequestedService::getId, rs -> rs));

// Track which IDs we keep (also Integer)
        Set<Integer> keepIds = new HashSet<>();

        if (dto.getRequestedServices() != null) {
            for (RequestedServiceEditDTO rsDto : dto.getRequestedServices()) {

                if (rsDto.getServiceId() <= 0) {
                    continue;
                }

                RequestedService rs;
                Integer rsDtoId = rsDto.getId();

                if (rsDtoId != null && rsDtoId > 0 && existingById.containsKey(rsDtoId)) {
                    rs = existingById.get(rsDtoId);
                } else {
                    rs = new RequestedService();
                    rs.setAppointment(appointment);

                    Service service = servicesService.getServiceById(rsDto.getServiceId());
                    rs.setService(service);
                }

                rs.setPriceAtBooking(rsDto.getPriceAtBooking());

                if (rsDto.getResponsibleStaffId() > 0) {
                    Staff staff = staffService.findById(rsDto.getResponsibleStaffId());
                    rs.setResponsibleStaff(staff);
                } else {
                    rs.setResponsibleStaff(null);
                }

                requestedServicesService.save(rs);

                keepIds.add(rs.getId());
            }
        }


// delete any requested services removed from the form
        for (RequestedService rs : existing) {
            if (!keepIds.contains(rs.getId())) {
                requestedServicesService.delete(rs);
            }
        }


        return "redirect:/manager/registration/view/" + id;
    }


    @GetMapping("/user-search")
    @ResponseBody
    public List<SimpleUserDTO> searchUsers(@RequestParam String phone) {
        List<UserAccount> results = userAccountService.getSomeByPhoneNumber(phone);
        List<SimpleUserDTO> userDTOS = new ArrayList<>();
        for (UserAccount result : results) {
            userDTOS.add(dtoMapper.toSimpleUserDTO(result));
        }
        return userDTOS;
    }
}
