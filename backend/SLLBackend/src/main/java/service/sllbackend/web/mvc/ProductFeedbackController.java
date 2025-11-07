package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.sllbackend.service.ProductFeedbackService;

import java.security.Principal;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductFeedbackController {
    
    private final ProductFeedbackService productFeedbackService;
    
    @PostMapping("/rate")
    public String submitRating(
            @RequestParam Integer productId,
            @RequestParam Short rating,
            @RequestParam(required = false) String comment,
            Principal principal) {
        
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        try {
            productFeedbackService.submitFeedback(
                    principal.getName(),
                    productId,
                    rating,
                    comment
            );
            return "redirect:/products/" + productId + "?ratingSuccess=true";
        } catch (Exception e) {
            return "redirect:/products/" + productId + "?ratingError=" + e.getMessage();
        }
    }
}
