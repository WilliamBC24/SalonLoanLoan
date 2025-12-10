package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.ProductImage;
import service.sllbackend.repository.ProductImageRepo;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.service.ProductImageService;

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
public class ProductImageServiceImpl implements ProductImageService {
    
    private final ProductRepo productRepo;
    private final ProductImageRepo productImageRepo;
    
    @Value("${product.image.upload-dir:uploads/product-images/}")
    private String uploadDir = "uploads/product-images/";
    
    @Value("${product.image.max-file-size:5242880}")
    private long maxFileSize = 5242880L; // 5MB in bytes
    
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    @Override
    @Transactional
    public ProductImage addImage(Integer productId, MultipartFile file) throws IOException {
        Product product = validateProductAndGetEntity(productId);
        String imagePath = saveFile(file, productId);
        
        ProductImage image = ProductImage.builder()
                .product(product)
                .imagePath(imagePath)
                .build();
        
        return productImageRepo.save(image);
    }

    @Override
    public List<ProductImage> getImages(Integer productId) {
        return productImageRepo.findByProductId(productId);
    }

    @Override
    @Transactional
    public void deleteImage(Integer imageId) {
        ProductImage image = productImageRepo.findById(imageId)
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
        productImageRepo.delete(image);
    }

    private Product validateProductAndGetEntity(Integer productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
    }

    private String saveFile(MultipartFile file, Integer productId) throws IOException {
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
        String filename = "product-" + productId + "-" + UUID.randomUUID() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Saved product image to: {}", filePath);
        return filePath.toString();
    }
}
