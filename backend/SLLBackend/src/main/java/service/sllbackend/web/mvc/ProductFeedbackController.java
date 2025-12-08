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
                log.info("Processing {} image(s) for feedback {}", images.size(), feedback.getId());
                int successCount = 0;
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        try {
                            productFeedbackImageService.addImage(feedback.getId(), image);
                            successCount++;
                            log.info("Successfully uploaded image {} for feedback {}", image.getOriginalFilename(), feedback.getId());
                        } catch (Exception e) {
                            log.error("Error uploading image {} for feedback {}: {}", image.getOriginalFilename(), feedback.getId(), e.getMessage(), e);
                            // Continue uploading other images even if one fails
                        }
                    }
                }
                log.info("Successfully uploaded {}/{} images for feedback {}", successCount, images.size(), feedback.getId());
            } else {
                log.info("No images provided for feedback {}", feedback.getId());
            }
            
            return "redirect:/products/" + productId + "?ratingSuccess=true";
        } catch (Exception e) {
            return "redirect:/products/" + productId + "?ratingError=" + e.getMessage();
        }
    }
}
