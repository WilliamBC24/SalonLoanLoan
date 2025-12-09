package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.ProductFeedbackImage;
import service.sllbackend.service.ProductFeedbackImageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product-feedback")
@RequiredArgsConstructor
@Slf4j
public class ProductFeedbackImageController {

    private final ProductFeedbackImageService productFeedbackImageService;

    @PostMapping("/{feedbackId}/images")
    public ResponseEntity<?> addImage(
            @PathVariable Integer feedbackId,
            @RequestParam("file") MultipartFile file) {
        try {
            ProductFeedbackImage image = productFeedbackImageService.addImage(feedbackId, file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Product feedback image added successfully");
            response.put("imageId", image.getId());
            response.put("imagePath", image.getImagePath());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error adding product feedback image: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (IOException e) {
            log.error("Error saving file: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error saving file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{feedbackId}/images")
    public ResponseEntity<?> getImages(@PathVariable Integer feedbackId) {
        try {
            List<ProductFeedbackImage> images = productFeedbackImageService.getImages(feedbackId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("images", images);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving product feedback images: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
