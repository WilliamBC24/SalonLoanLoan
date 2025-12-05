package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.ProductFeedback;
import service.sllbackend.entity.ProductFeedbackImage;
import service.sllbackend.repository.ProductFeedbackImageRepo;
import service.sllbackend.service.ImageStorageService;
import service.sllbackend.service.ProductFeedbackService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductFeedbackController {
    
    private final ProductFeedbackService productFeedbackService;
    private final ImageStorageService imageStorageService;
    private final ProductFeedbackImageRepo productFeedbackImageRepo;
    
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
            
            // Handle image uploads
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (image != null && !image.isEmpty()) {
                        try {
                            String imagePath = imageStorageService.storeImage(image, "reviews");
                            ProductFeedbackImage feedbackImage = ProductFeedbackImage.builder()
                                .productFeedback(feedback)
                                .imagePath(imagePath)
                                .build();
                            productFeedbackImageRepo.save(feedbackImage);
                            log.info("Saved review image: {} for feedback: {}", imagePath, feedback.getId());
                        } catch (IOException e) {
                            log.error("Failed to store image for feedback: {}", feedback.getId(), e);
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
