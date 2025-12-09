package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.AfterAppointmentImage;
import service.sllbackend.entity.BeforeAppointmentImage;
import service.sllbackend.service.AppointmentImageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentImageController {

    private final AppointmentImageService appointmentImageService;

    @PostMapping("/{appointmentId}/images/before")
    public ResponseEntity<?> addBeforeImage(
            @PathVariable Integer appointmentId,
            @RequestParam("file") MultipartFile file) {
        try {
            BeforeAppointmentImage image = appointmentImageService.addBeforeImage(appointmentId, file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Before service image added successfully");
            response.put("imageId", image.getId());
            response.put("imagePath", image.getImagePath());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Error adding before image: {}", e.getMessage());
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

    @PostMapping("/{appointmentId}/images/after")
    public ResponseEntity<?> addAfterImage(
            @PathVariable Integer appointmentId,
            @RequestParam("file") MultipartFile file) {
        try {
            AfterAppointmentImage image = appointmentImageService.addAfterImage(appointmentId, file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "After service image added successfully");
            response.put("imageId", image.getId());
            response.put("imagePath", image.getImagePath());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Error adding after image: {}", e.getMessage());
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

    @GetMapping("/{appointmentId}/images/before")
    public ResponseEntity<?> getBeforeImages(@PathVariable Integer appointmentId) {
        try {
            List<BeforeAppointmentImage> images = appointmentImageService.getBeforeImages(appointmentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("images", images);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving before images: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{appointmentId}/images/after")
    public ResponseEntity<?> getAfterImages(@PathVariable Integer appointmentId) {
        try {
            List<AfterAppointmentImage> images = appointmentImageService.getAfterImages(appointmentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("images", images);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving after images: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving images: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
