package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.ProductFeedback;
import service.sllbackend.entity.ProductFeedbackImage;

import java.util.List;

@Repository
public interface ProductFeedbackImageRepo extends JpaRepository<ProductFeedbackImage, Integer> {
    List<ProductFeedbackImage> findByProductFeedback(ProductFeedback productFeedback);
    List<ProductFeedbackImage> findByProductFeedbackId(Integer productFeedbackId);
    void deleteByProductFeedback(ProductFeedback productFeedback);
}
