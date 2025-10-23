package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.sllbackend.service.impl.RegisterServiceImpl;
import service.sllbackend.web.dto.UserRegisterDTO;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final RegisterServiceImpl registerService;

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

    @GetMapping("/user/register")
    public String userRegister(){
        return "user-register";
    }

    @PostMapping("/user/register/create")
    public String userAccountCreation(RedirectAttributes redirectAttributes,
                                      @Valid @ModelAttribute UserRegisterDTO registerDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Register error, check your inputs");
            return "redirect:/auth/user/register";
        }

        try {
            registerService.registerUser(registerDTO);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Register error, check your inputs");
            return "redirect:/auth/user/register";
        }
        redirectAttributes.addFlashAttribute("success", "Register success");
        return "redirect:/auth/user/register";
    }
}
