package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

        return "index";
    }

    @GetMapping("index")
    public String index(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        model.addAttribute("services", servicesService.getTenServices());
        model.addAttribute("products", productsService.getTenProducts());

        return "index";
    }

    @GetMapping("search")
    public String globalSearch(@RequestParam(required = false) String q, Model model) {
        if (q != null && !q.trim().isEmpty()) {
            // Redirect to services page with search query
            return "redirect:/services?name=" + q.trim();
        }
        // If no search query, redirect to home
        return "redirect:/";
    }
}
