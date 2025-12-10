package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.ServiceImage;
import service.sllbackend.service.ServiceImageService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceImageController {

    private final ServiceImageService serviceImageService;

    @PostMapping("/{serviceId}/images")
    public ResponseEntity<?> addImage(
            @PathVariable Integer serviceId,
            @RequestParam("file") MultipartFile file) {
        try {
            ServiceImage image = serviceImageService.addImage(serviceId, file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Service image added successfully");
            response.put("imageId", image.getId());
            response.put("imagePath", image.getImagePath());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error adding service image: {}", e.getMessage());
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

    @GetMapping("/{serviceId}/images")
    public ResponseEntity<?> getImages(@PathVariable Integer serviceId) {
        try {
            List<ServiceImage> images = serviceImageService.getImages(serviceId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("images", images);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving service images: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer imageId) {
        try {
            serviceImageService.deleteImage(imageId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Service image deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error deleting service image: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("Error deleting service image: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error deleting image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/images/file/{imageId}")
    public ResponseEntity<Resource> getImageFile(@PathVariable Integer imageId) {
        try {
            List<ServiceImage> allImages = serviceImageService.getImages(null);
            ServiceImage image = allImages.stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Image not found"));

            Path filePath = Paths.get(image.getImagePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName().toString() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("File not found or not readable");
            }
        } catch (Exception e) {
            log.error("Error retrieving image file: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}
