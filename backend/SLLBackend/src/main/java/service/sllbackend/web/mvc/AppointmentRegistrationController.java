package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointment")
public class AppointmentRegistrationController {

    @GetMapping
    public String appointmentRegistration(){
        return "appointment-register";
    }
}
