package service.sllbackend.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.sllbackend.auth.service.UserAccountService;

@RestController
@RequestMapping("/api")
public class AuthRestController {
    private final UserAccountService userAccountService;

    public AuthRestController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/add")
    public void add(){
        userAccountService.registerUser("admin", "admin");
    }
}
