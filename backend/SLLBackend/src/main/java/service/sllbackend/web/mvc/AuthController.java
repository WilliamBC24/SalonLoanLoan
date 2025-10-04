package service.sllbackend.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth/")
public class AuthController {
    @GetMapping("/staff/login")
    public String staffLogin(){
        return "staff-login";
    }

    @GetMapping("/user/login")
    public String userLogin(){
        return "user-login";
    }
}
