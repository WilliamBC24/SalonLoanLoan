package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.ProductFeedback;
import service.sllbackend.entity.ProductFeedbackImage;
import service.sllbackend.repository.ProductFeedbackImageRepo;
import service.sllbackend.repository.ProductFeedbackRepo;
import service.sllbackend.service.ProductFeedbackImageService;

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
public class ProductFeedbackImageServiceImpl implements ProductFeedbackImageService {
    
    private final ProductFeedbackRepo productFeedbackRepo;
    private final ProductFeedbackImageRepo productFeedbackImageRepo;
    
    // Field initialization provides defaults for unit tests where @Value is not injected
    @Value("${product.feedback.image.upload-dir:uploads/product-feedback-images/}")
    private String uploadDir = "uploads/product-feedback-images/";
    
    @Value("${product.feedback.image.max-file-size:5242880}") // 5MB default
    private long maxFileSize = 5242880L; // 5MB in bytes
    
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    @Override
    @Transactional
    public ProductFeedbackImage addImage(Integer productFeedbackId, MultipartFile file) throws IOException {
        ProductFeedback productFeedback = validateProductFeedbackAndGetEntity(productFeedbackId);
        String imagePath = saveFile(file);
        
        ProductFeedbackImage image = ProductFeedbackImage.builder()
                .productFeedback(productFeedback)
                .imagePath(imagePath)
                .build();
        
        return productFeedbackImageRepo.save(image);
    }

    @Override
    public List<ProductFeedbackImage> getImages(Integer productFeedbackId) {
        return productFeedbackImageRepo.findByProductFeedbackId(productFeedbackId);
    }

    private ProductFeedback validateProductFeedbackAndGetEntity(Integer productFeedbackId) {
        return productFeedbackRepo.findById(productFeedbackId)
                .orElseThrow(() -> new IllegalArgumentException("Product feedback not found with id: " + productFeedbackId));
    }

    private String saveFile(MultipartFile file) throws IOException {
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
        String filename = "product-feedback-" + UUID.randomUUID() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Saved product feedback image to: {}", filePath);
        // Return just the filename, not the full path
        return filename;
    }
}
