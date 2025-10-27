package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.AppointmentDetails;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.service.impl.AppointmentDetailsServiceImpl;
import service.sllbackend.service.impl.AppointmentServiceImpl;
import service.sllbackend.utils.DTOMapper;
import service.sllbackend.web.dto.AppointmentDetailsEditDTO;
import service.sllbackend.web.dto.AppointmentDetailsViewDTO;

import java.util.List;

@Controller
@RequestMapping("/staff/registration")
@RequiredArgsConstructor
public class StaffRegistrationManagementController {
    private final AppointmentServiceImpl appointmentService;
    private final AppointmentDetailsServiceImpl appointmentDetailsService;
    private final DTOMapper dtoMapper;

    @GetMapping("/list")
    public String staffRegistrationList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<AppointmentStatus> statuses,
            Model model) {

        List<Appointment> registrations = appointmentService.getByNameAndStatus(name, statuses);

        model.addAttribute("registrations", registrations);
        model.addAttribute("allStatuses", AppointmentStatus.values());

        return "staff-registration-list";
    }

    @GetMapping("/view/{id}")
    public String staffRegistrationView(@PathVariable long id, Model model) {
        Appointment appointment = appointmentService.findById(id);
        AppointmentDetails appointmentDetails = appointmentDetailsService.findByAppointmentId(id);
        UserAccount user = appointmentDetails.getUser();
        AppointmentDetailsViewDTO appointmentDetailsDTO = dtoMapper.toAppointmentDetailsViewDTO(appointmentDetails, user);


        model.addAttribute("appointment", appointment);
        model.addAttribute("appointmentDetails", appointmentDetailsDTO);
        return "staff-registration-edit";
    }

    @PostMapping("/save/{id}")
    public String staffRegistrationSave(@PathVariable long id,
                                        @Valid @ModelAttribute("appointmentDetails") AppointmentDetailsEditDTO dto,
                                        Model model) {
        Appointment appointment = appointmentService.findById(id);
        if (dto.getPhoneNumber() != null) appointment.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getScheduledAt() != null) appointment.setScheduledAt(dto.getScheduledAt());
        if (dto.getStatus() != null) appointment.setStatus(dto.getStatus());
        appointmentService.save(appointment);

        AppointmentDetails details = appointmentDetailsService.findByAppointmentId(id);
        if (dto.getActualStart() != null) details.setActualStart(dto.getActualStart());
        if (dto.getActualEnd() != null) details.setActualEnd(dto.getActualEnd());
//        if (dto.getUsername() != null) {
//            UserAccount user = userAccountService.findByUsername(dto.getUsername());
//            details.setUser(user);
//        }
        appointmentDetailsService.save(details);

        return "redirect:/staff/registration/view/" + id;
    }
}
