package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.AfterAppointmentImage;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.BeforeAppointmentImage;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.repository.AfterAppointmentImageRepo;
import service.sllbackend.repository.AppointmentRepo;
import service.sllbackend.repository.BeforeAppointmentImageRepo;
import service.sllbackend.service.AppointmentImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentImageServiceImpl implements AppointmentImageService {
    
    private final AppointmentRepo appointmentRepo;
    private final BeforeAppointmentImageRepo beforeAppointmentImageRepo;
    private final AfterAppointmentImageRepo afterAppointmentImageRepo;
    
    @Value("${appointment.image.upload-dir:uploads/appointment-images/}")
    private String uploadDir = "uploads/appointment-images/";
    
    @Value("${appointment.image.max-file-size:5242880}") // 5MB default
    private long maxFileSize = 5242880L;
    
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    @Override
    @Transactional
    public BeforeAppointmentImage addBeforeImage(Integer appointmentId, MultipartFile file) throws IOException {
        Appointment appointment = validateAppointmentAndGetEntity(appointmentId);
        String imagePath = saveFile(file, "before");
        
        BeforeAppointmentImage image = BeforeAppointmentImage.builder()
                .appointment(appointment)
                .imagePath(imagePath)
                .build();
        
        return beforeAppointmentImageRepo.save(image);
    }

    @Override
    @Transactional
    public AfterAppointmentImage addAfterImage(Integer appointmentId, MultipartFile file) throws IOException {
        Appointment appointment = validateAppointmentAndGetEntity(appointmentId);
        String imagePath = saveFile(file, "after");
        
        AfterAppointmentImage image = AfterAppointmentImage.builder()
                .appointment(appointment)
                .imagePath(imagePath)
                .build();
        
        return afterAppointmentImageRepo.save(image);
    }

    @Override
    public List<BeforeAppointmentImage> getBeforeImages(Integer appointmentId) {
        return beforeAppointmentImageRepo.findByAppointmentId(appointmentId);
    }

    @Override
    public List<AfterAppointmentImage> getAfterImages(Integer appointmentId) {
        return afterAppointmentImageRepo.findByAppointmentId(appointmentId);
    }

    private Appointment validateAppointmentAndGetEntity(Integer appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));
        
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Images can only be added to appointments with COMPLETED status. Current status: " + appointment.getStatus());
        }
        
        return appointment;
    }

    private String saveFile(MultipartFile file, String imageType) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file size
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + maxFileSize + " bytes");
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Invalid file type. Only image files are allowed: " + ALLOWED_CONTENT_TYPES);
        }

        // Validate and sanitize file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid filename");
        }
        
        // Extract and validate extension securely
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < originalFilename.length() - 1) {
            extension = originalFilename.substring(lastDotIndex).toLowerCase(Locale.ROOT);
        }
        
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Invalid file extension. Only image files are allowed: " + ALLOWED_EXTENSIONS);
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename with sanitized extension
        String filename = imageType + "-" + UUID.randomUUID() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Saved {} image to: {}", imageType, filePath);
        return filePath.toString();
    }
}
