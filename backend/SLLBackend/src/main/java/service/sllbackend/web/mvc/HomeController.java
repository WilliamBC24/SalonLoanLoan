package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import service.sllbackend.service.impl.ProductsServiceImpl;
import service.sllbackend.service.impl.ServicesServiceImpl;

import java.security.Principal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {
    private final ServicesServiceImpl servicesService;
    private final ProductsServiceImpl productsService;

    @GetMapping()
    public String home(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        model.addAttribute("services", servicesService.getTenServices());
        model.addAttribute("products", productsService.getTenProducts());

        return "home";
    }
}
