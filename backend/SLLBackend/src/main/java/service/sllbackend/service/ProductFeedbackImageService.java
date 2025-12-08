package service.sllbackend.service;

import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.ProductFeedbackImage;

import java.io.IOException;
import java.util.List;

public interface ProductFeedbackImageService {
    
    /**
     * Add an image to a product feedback
     * @param productFeedbackId Product feedback ID
     * @param file Image file to upload
     * @return Saved product feedback image
     * @throws IOException if file cannot be saved
     */
    ProductFeedbackImage addImage(Integer productFeedbackId, MultipartFile file) throws IOException;
    
    /**
     * Get all images for a product feedback
     * @param productFeedbackId Product feedback ID
     * @return List of product feedback images
     */
    List<ProductFeedbackImage> getImages(Integer productFeedbackId);
}
