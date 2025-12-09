package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.ProductFeedbackImage;

import java.util.List;

@Repository
public interface ProductFeedbackImageRepo extends JpaRepository<ProductFeedbackImage, Integer> {
    
    /**
     * Find all images for a specific product feedback
     * @param productFeedbackId Product feedback ID
     * @return List of product feedback images
     */
    List<ProductFeedbackImage> findByProductFeedbackId(Integer productFeedbackId);
}
