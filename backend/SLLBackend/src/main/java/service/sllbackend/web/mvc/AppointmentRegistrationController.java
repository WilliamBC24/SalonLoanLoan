package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.service.AppointmentService;
import service.sllbackend.web.dto.AppointmentRegisterDTO;
import service.sllbackend.web.dto.AvailableStaffDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointment")
public class AppointmentRegistrationController {
    private final AppointmentService appointmentService;

    @GetMapping
    public String appointmentRegistrationView(){
        return "appointment-register";
    }

    @PostMapping("/book")
    public String appointmentRegistration(
            @Valid @ModelAttribute AppointmentRegisterDTO appointmentRegisterDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "redirect:/appointment?error";
        }

        try {
            appointmentService.register(appointmentRegisterDTO);
        } catch (Exception e) {
            return "redirect:/appointment?error";
        }

        return "redirect:/appointment?success";
    }

    @GetMapping(
            value = "/available-staff",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public List<AvailableStaffDTO> getAvailableStaff(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("startTime")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam("durationMinutes") int durationMinutes
    ) {
        return appointmentService.findAvailableStaff(date, startTime, durationMinutes);
    }
}
