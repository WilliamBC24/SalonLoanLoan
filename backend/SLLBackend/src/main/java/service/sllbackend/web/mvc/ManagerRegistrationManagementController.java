package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import service.sllbackend.web.dto.SimpleUserDTO;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/manager/registration")
@RequiredArgsConstructor
public class ManagerRegistrationManagementController {
    private final AppointmentService appointmentService;
    private final AppointmentDetailsService appointmentDetailsService;
    private final UserAccountService userAccountService;
    private final RequestedServicesService requestedServicesService;
    private final StaffAccountService staffAccountService;
    private final DTOMapper dtoMapper;

    @GetMapping("/list")
    public String staffRegistrationList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<AppointmentStatus> statuses,
            Model model) {

        List<Appointment> registrations = appointmentService.getByNameAndStatus(name == null ? name : name.trim(), statuses);

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

        if (appointmentDetails != null && appointmentDetails.getScheduledStart() != null) {
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
    public String staffRegistrationSave(@PathVariable long id,
                                        @Valid @ModelAttribute("appointmentDetails") AppointmentDetailsEditDTO dto,
                                        BindingResult bindingResult,
                                        Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/manager/registration/view/" + id;
        }
        Appointment appointment = appointmentService.findById(id);
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) appointment.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getScheduledAt() != null) appointment.setScheduledAt(dto.getScheduledAt());
        if (dto.getStatus() != null) appointment.setStatus(dto.getStatus());
        appointmentService.save(appointment);

        AppointmentDetails details = appointmentDetailsService.findByAppointmentId(id);
        if (dto.getActualStart() != null) details.setActualStart(dto.getActualStart());
        if (dto.getActualEnd() != null) details.setActualEnd(dto.getActualEnd());
        if (dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            UserAccount user = userAccountService.findByUsername(dto.getUsername());
            details.setUser(user);
        }
        appointmentDetailsService.save(details);

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
