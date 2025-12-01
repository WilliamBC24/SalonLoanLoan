package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.AppointmentDetails;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.service.AppointmentDetailsService;
import service.sllbackend.service.AppointmentService;
import service.sllbackend.service.UserAccountService;
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
    private final DTOMapper dtoMapper;

    @GetMapping("/list")
    public String staffRegistrationList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<AppointmentStatus> statuses,
            Model model) {

        List<Appointment> registrations = appointmentService.getByNameAndStatus(name == null ? name : name.trim(), statuses);

        model.addAttribute("registrations", registrations);
        model.addAttribute("allStatuses", AppointmentStatus.values());

        return "staff-registration-list";
    }

    @GetMapping("/view/{id}")
    public String staffRegistrationView(@PathVariable long id, Model model) {
        Appointment appointment = appointmentService.findById(id);
        AppointmentDetails appointmentDetails = appointmentDetailsService.findByAppointmentId(id);
        if (appointmentDetails.getScheduledStart() != null) {
            UserAccount user = appointmentDetails.getUser();
            AppointmentDetailsViewDTO appointmentDetailsDTO = dtoMapper.toAppointmentDetailsViewDTO(appointmentDetails, user);
            model.addAttribute("appointmentDetails", appointmentDetailsDTO);
        } else {
            model.addAttribute("appointmentDetails", new AppointmentDetailsViewDTO());
            model.addAttribute("disableDetails", true);
        }

        model.addAttribute("appointment", appointment);
        return "staff-registration-edit";
    }

    @PostMapping("/save/{id}")
    public String staffRegistrationSave(@PathVariable long id,
                                        @Valid @ModelAttribute("appointmentDetails") AppointmentDetailsEditDTO dto,
                                        BindingResult bindingResult,
                                        Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/staff/registration/view/" + id;
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

        return "redirect:/staff/registration/view/" + id;
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
