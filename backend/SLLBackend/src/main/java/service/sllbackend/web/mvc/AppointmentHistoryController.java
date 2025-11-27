package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("")
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

    @GetMapping("/view/{id}")
    public String viewAppointmentDetails(
            @PathVariable Integer id,
            Model model,
            Principal principal
    ) {
        var appointmentDetails = appointmentService.getDetailsByAppointmentId(id);

        if (appointmentDetails == null) {
            return "redirect:/user/appointment-history?error=Appointment not found";
        }

        if (!appointmentDetails.getAppointment().getPhoneNumber().equals(principal.getName())
                && !appointmentDetails.getAppointment().getName().equalsIgnoreCase(principal.getName())) {

            return "redirect:/user/appointment-history?error=Unauthorized access";
        }

        var requestedServices = appointmentService.getRequestedServices(id);

        Integer totalDuration = requestedServices.stream()
                .map(rs -> rs.getService().getDurationMinutes() == null ? 0 : rs.getService().getDurationMinutes().intValue())
                .reduce(0, Integer::sum);

        Integer totalPrice = requestedServices.stream()
                .map(rs -> rs.getPriceAtBooking() == null ? 0 : rs.getPriceAtBooking())
                .reduce(0, Integer::sum);

        model.addAttribute("appointmentDetails", appointmentDetails);
        model.addAttribute("requestedServices", requestedServices);
        model.addAttribute("totalDuration", totalDuration);
        model.addAttribute("totalPrice", totalPrice);

        return "user-appointment-history-detail";
    }

}
