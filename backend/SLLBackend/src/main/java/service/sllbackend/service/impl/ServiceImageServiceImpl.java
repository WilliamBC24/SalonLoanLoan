package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceImage;
import service.sllbackend.repository.ServiceImageRepo;
import service.sllbackend.repository.ServiceRepo;
import service.sllbackend.service.ServiceImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ServiceImageServiceImpl implements ServiceImageService {
    
    private final ServiceRepo serviceRepo;
    private final ServiceImageRepo serviceImageRepo;
    
    @Value("${service.image.upload-dir:uploads/service-images/}")
    private String uploadDir = "uploads/service-images/";
    
    @Value("${service.image.max-file-size:5242880}")
    private long maxFileSize = 5242880L; // 5MB in bytes
    
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );
    
    @jakarta.annotation.PostConstruct
    public void init() {
        // Ensure upload directory exists at startup
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created service image upload directory: {}", uploadPath);
            }
        } catch (IOException e) {
            log.error("Failed to create service image upload directory", e);
        }
    }

    @Override
    @Transactional
    public ServiceImage addImage(Integer serviceId, MultipartFile file) throws IOException {
        Service service = validateServiceAndGetEntity(serviceId);
        String imagePath = saveFile(file, serviceId);
        
        ServiceImage image = ServiceImage.builder()
                .service(service)
                .imagePath(imagePath)
                .build();
        
        return serviceImageRepo.save(image);
    }

    @Override
    public List<ServiceImage> getImages(Integer serviceId) {
        return serviceImageRepo.findByServiceId(serviceId);
    }

    @Override
    public ServiceImage getImageById(Integer imageId) {
        return serviceImageRepo.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + imageId));
    }

    @Override
    @Transactional
    public void deleteImage(Integer imageId) {
        ServiceImage image = serviceImageRepo.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + imageId));
        
        // Delete the physical file
        try {
            Path filePath = Paths.get(image.getImagePath());
            Files.deleteIfExists(filePath);
            log.info("Deleted image file: {}", filePath);
        } catch (IOException e) {
            log.error("Error deleting image file: {}", e.getMessage(), e);
        }
        
        // Delete the database record
        serviceImageRepo.delete(image);
    }

    private Service validateServiceAndGetEntity(Integer serviceId) {
        return serviceRepo.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found with id: " + serviceId));
    }

    private String saveFile(MultipartFile file, Integer serviceId) throws IOException {
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

        // Use absolute path for upload directory
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename with sanitized extension
        String filename = "service-" + serviceId + "-" + UUID.randomUUID() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return web-accessible path instead of file system path
        // Ensure uploadDir ends with / before appending filename
        String normalizedUploadDir = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        String webPath = "/" + normalizedUploadDir + filename;
        log.info("Saved service image to: {} (web path: {})", filePath.toAbsolutePath(), webPath);
        return webPath;
    }
}
