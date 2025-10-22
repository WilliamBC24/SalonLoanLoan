package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.sllbackend.entity.Appointment;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.service.AppointmentService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user/appointment-history")
@RequiredArgsConstructor
public class AppointmentHistoryController {
    private final AppointmentService appointmentService;

    @GetMapping("/user/appointments")
    public String userAppointmentHistory(
            @RequestParam(required = false) List<AppointmentStatus> statuses,
            Model model,
            Principal principal) {

        List<Appointment> appointments = appointmentService.getByNameAndStatus(principal.getName(), statuses);

        model.addAttribute("appointments", appointments);
        model.addAttribute("allStatuses", AppointmentStatus.values());
        model.addAttribute("selectedStatuses", statuses);

        return "user-appointment-history";
    }
}
