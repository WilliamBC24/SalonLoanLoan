package service.sllbackend.web.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

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

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String service, Model model){
        if (service != null) {
            model.addAttribute("selectedService", service);
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("phone") String phone,
            @RequestParam("password") String password,
            @RequestParam(required = false) String service,
            Model model) {
        // Xử lý logic đăng nhập ở đây
        // Tạm thời redirect về trang chủ với thông báo thành công
        if (service != null && !service.isEmpty()) {
            model.addAttribute("success", "Đăng nhập thành công! Dịch vụ: " + service);
        } else {
            model.addAttribute("success", "Đăng nhập thành công!");
        }
        return "redirect:/?login=success";
    }

}
