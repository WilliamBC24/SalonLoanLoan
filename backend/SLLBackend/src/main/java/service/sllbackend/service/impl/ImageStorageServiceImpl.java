package service.sllbackend.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.service.ImageStorageService;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class ImageStorageServiceImpl implements ImageStorageService {
    
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    @Value("${image.upload.directory:uploads/images}")
    private String uploadDirectory;
    
    private Path uploadPath;
    
    @PostConstruct
    public void init() {
        try {
            uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            log.info("Image upload directory initialized at: {}", uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDirectory, e);
        }
    }
    
    @Override
    public String storeImage(MultipartFile file, String subDirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        
        if (!isValidImage(file)) {
            throw new IllegalArgumentException("Invalid image file. Allowed types: JPEG, PNG, GIF, WEBP. Max size: 5MB");
        }
        
        // Create subdirectory if needed
        Path targetLocation = uploadPath.resolve(subDirectory);
        Files.createDirectories(targetLocation);
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // Store file
        Path filePath = targetLocation.resolve(uniqueFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Return relative path for storage in database
        String relativePath = subDirectory + "/" + uniqueFilename;
        log.info("Stored image: {}", relativePath);
        
        return relativePath;
    }
    
    @Override
    public List<String> storeImages(List<MultipartFile> files, String subDirectory) throws IOException {
        List<String> storedPaths = new ArrayList<>();
        
        if (files == null || files.isEmpty()) {
            return storedPaths;
        }
        
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                storedPaths.add(storeImage(file, subDirectory));
            }
        }
        
        return storedPaths;
    }
    
    @Override
    public boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }
        
        try {
            Path filePath = uploadPath.resolve(imagePath).normalize();
            
            // Security check - ensure the path is still within upload directory
            if (!filePath.startsWith(uploadPath)) {
                log.warn("Attempted to delete file outside upload directory: {}", imagePath);
                return false;
            }
            
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Deleted image: {}", imagePath);
            }
            return deleted;
        } catch (IOException e) {
            log.error("Failed to delete image: {}", imagePath, e);
            return false;
        }
    }
    
    @Override
    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }
        
        // Check content type
        String contentType = file.getContentType();
        return contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase());
    }
    
    @Override
    public byte[] getImageContent(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            throw new IllegalArgumentException("Image path cannot be empty");
        }
        
        Path filePath = uploadPath.resolve(imagePath).normalize();
        
        // Security check
        if (!filePath.startsWith(uploadPath)) {
            throw new SecurityException("Access denied to path: " + imagePath);
        }
        
        if (!Files.exists(filePath)) {
            throw new IOException("Image not found: " + imagePath);
        }
        
        return Files.readAllBytes(filePath);
    }
    
    @Override
    public String getContentType(String imagePath) {
        if (imagePath == null) {
            return "application/octet-stream";
        }
        
        String lowerPath = imagePath.toLowerCase();
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith(".png")) {
            return "image/png";
        } else if (lowerPath.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerPath.endsWith(".webp")) {
            return "image/webp";
        }
        
        return "application/octet-stream";
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ".jpg";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            return filename.substring(dotIndex).toLowerCase();
        }
        return ".jpg";
    }
}
