package service.sllbackend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageStorageService {
    
    /**
     * Store an image file and return the stored path
     * @param file MultipartFile to store
     * @param subDirectory subdirectory for categorization (e.g., "products", "services", "reviews")
     * @return The stored file path
     * @throws IOException if storage fails
     */
    String storeImage(MultipartFile file, String subDirectory) throws IOException;
    
    /**
     * Store multiple image files and return the stored paths
     * @param files List of MultipartFiles to store
     * @param subDirectory subdirectory for categorization
     * @return List of stored file paths
     * @throws IOException if storage fails
     */
    List<String> storeImages(List<MultipartFile> files, String subDirectory) throws IOException;
    
    /**
     * Delete an image by its path
     * @param imagePath The path of the image to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean deleteImage(String imagePath);
    
    /**
     * Validate that the file is a valid image
     * @param file MultipartFile to validate
     * @return true if valid image, false otherwise
     */
    boolean isValidImage(MultipartFile file);
    
    /**
     * Get the byte content of an image
     * @param imagePath The path of the image
     * @return byte array of image content
     * @throws IOException if reading fails
     */
    byte[] getImageContent(String imagePath) throws IOException;
    
    /**
     * Get the content type of an image
     * @param imagePath The path of the image
     * @return Content type string (e.g., "image/jpeg")
     */
    String getContentType(String imagePath);
}
