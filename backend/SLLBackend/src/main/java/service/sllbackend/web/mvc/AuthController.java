package service.sllbackend.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth/")
public class AuthController {
    @GetMapping("/staff/login")
    public String staffLogin(){
        return "staff-login";
    }

    @GetMapping("/staff/landing")
    public String staffLanding(){
        return "staff-landing";
    }

    @GetMapping("/user/login")
    public String userLogin(){
        return "user-login";
    }

    @GetMapping("/user/landing")
    public String userLanding(){
        return "user-landing";
    }

}
