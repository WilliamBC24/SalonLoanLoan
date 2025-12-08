package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.ProductFeedback;
import service.sllbackend.service.ProductFeedbackImageService;
import service.sllbackend.service.ProductFeedbackService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductFeedbackController {
    
    private final ProductFeedbackService productFeedbackService;
    private final ProductFeedbackImageService productFeedbackImageService;
    
    @PostMapping("/rate")
    public String submitRating(
            @RequestParam Integer productId,
            @RequestParam Short rating,
            @RequestParam(required = false) String comment,
            @RequestParam(required = false) List<MultipartFile> images,
            Principal principal) {
        
        if (principal == null) {
            return "redirect:/auth/user/login";
        }
        
        try {
            ProductFeedback feedback = productFeedbackService.submitFeedback(
                    principal.getName(),
                    productId,
                    rating,
                    comment
            );
            
            // Upload images if provided
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        try {
                            productFeedbackImageService.addImage(feedback.getId(), image);
                        } catch (Exception e) {
                            log.error("Error uploading image for feedback {}: {}", feedback.getId(), e.getMessage());
                            // Continue uploading other images even if one fails
                        }
                    }
                }
            }
            
            return "redirect:/products/" + productId + "?ratingSuccess=true";
        } catch (Exception e) {
            return "redirect:/products/" + productId + "?ratingError=" + e.getMessage();
        }
    }
}
