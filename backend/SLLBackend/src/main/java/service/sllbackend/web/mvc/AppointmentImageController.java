package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.AfterAppointmentImage;
import service.sllbackend.entity.BeforeAppointmentImage;
import service.sllbackend.service.AppointmentImageService;
import service.sllbackend.web.dto.AppointmentImageDTO;
import service.sllbackend.web.dto.AppointmentImageResponseDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentImageController {
    
    private final AppointmentImageService appointmentImageService;
    
    /**
     * Add a before-service image to an appointment.
     * Only allowed when appointment status is COMPLETED.
     */
    @PostMapping("/{appointmentId}/images/before")
    public ResponseEntity<?> addBeforeImage(
            @PathVariable Integer appointmentId,
            @Valid @RequestBody AppointmentImageDTO dto) {
        
        log.info("Adding before image to appointment {}: {}", appointmentId, dto.getImagePath());
        
        try {
            BeforeAppointmentImage image = appointmentImageService.addBeforeImage(
                    appointmentId, dto.getImagePath());
            
            AppointmentImageResponseDTO response = AppointmentImageResponseDTO.builder()
                    .id(image.getId())
                    .appointmentId(appointmentId)
                    .imagePath(image.getImagePath())
                    .imageType("before")
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            log.warn("Cannot add before image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("Appointment not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Add an after-service image to an appointment.
     * Only allowed when appointment status is COMPLETED.
     */
    @PostMapping("/{appointmentId}/images/after")
    public ResponseEntity<?> addAfterImage(
            @PathVariable Integer appointmentId,
            @Valid @RequestBody AppointmentImageDTO dto) {
        
        log.info("Adding after image to appointment {}: {}", appointmentId, dto.getImagePath());
        
        try {
            AfterAppointmentImage image = appointmentImageService.addAfterImage(
                    appointmentId, dto.getImagePath());
            
            AppointmentImageResponseDTO response = AppointmentImageResponseDTO.builder()
                    .id(image.getId())
                    .appointmentId(appointmentId)
                    .imagePath(image.getImagePath())
                    .imageType("after")
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            log.warn("Cannot add after image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("Appointment not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Get all images (before and after) for an appointment.
     */
    @GetMapping("/{appointmentId}/images")
    public ResponseEntity<?> getImages(@PathVariable Integer appointmentId) {
        log.info("Getting images for appointment {}", appointmentId);
        
        List<BeforeAppointmentImage> beforeImages = appointmentImageService.getBeforeImages(appointmentId);
        List<AfterAppointmentImage> afterImages = appointmentImageService.getAfterImages(appointmentId);
        
        List<AppointmentImageResponseDTO> beforeDtos = beforeImages.stream()
                .map(img -> AppointmentImageResponseDTO.builder()
                        .id(img.getId())
                        .appointmentId(appointmentId)
                        .imagePath(img.getImagePath())
                        .imageType("before")
                        .build())
                .collect(Collectors.toList());
        
        List<AppointmentImageResponseDTO> afterDtos = afterImages.stream()
                .map(img -> AppointmentImageResponseDTO.builder()
                        .id(img.getId())
                        .appointmentId(appointmentId)
                        .imagePath(img.getImagePath())
                        .imageType("after")
                        .build())
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("appointmentId", appointmentId);
        response.put("beforeImages", beforeDtos);
        response.put("afterImages", afterDtos);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete a before-service image.
     */
    @DeleteMapping("/images/before/{imageId}")
    public ResponseEntity<?> deleteBeforeImage(@PathVariable Integer imageId) {
        log.info("Deleting before image {}", imageId);
        
        try {
            appointmentImageService.deleteBeforeImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting before image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error deleting image"));
        }
    }
    
    /**
     * Delete an after-service image.
     */
    @DeleteMapping("/images/after/{imageId}")
    public ResponseEntity<?> deleteAfterImage(@PathVariable Integer imageId) {
        log.info("Deleting after image {}", imageId);
        
        try {
            appointmentImageService.deleteAfterImage(imageId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting after image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error deleting image"));
        }
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
