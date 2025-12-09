package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import service.sllbackend.service.EmailService;
import service.sllbackend.service.ProductsService;
import service.sllbackend.service.ServicesService;

import java.security.Principal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    private final ServicesService servicesService;
    private final ProductsService productsService;
    private final EmailService emailService;

    @GetMapping()
    public String home(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        model.addAttribute("services", servicesService.getServices("", 4));
        model.addAttribute("products", productsService.getProducts("", 4));

        model.addAttribute("lat", 21.018502);
        model.addAttribute("lng", 105.811386);

        return "home";
    }
}
