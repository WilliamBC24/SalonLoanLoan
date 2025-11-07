package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import service.sllbackend.service.AppointmentService;
import service.sllbackend.web.dto.AppointmentRegisterDTO;

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
    public String appointmentRegistration(@Valid @ModelAttribute AppointmentRegisterDTO appointmentRegisterDTO,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            return "appointment-register";
        }
        try{
            appointmentService.register(appointmentRegisterDTO);
        } catch (Exception e) {
            return "redirect:/appointment";
        }
        return "redirect:/appointment";
    }
}
