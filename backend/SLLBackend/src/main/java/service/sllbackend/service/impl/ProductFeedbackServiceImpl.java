package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.OrderStatus;
import service.sllbackend.repository.OrderInvoiceDetailsRepo;
import service.sllbackend.repository.ProductFeedbackRepo;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.service.ProductFeedbackService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFeedbackServiceImpl implements ProductFeedbackService {
    
    private final ProductFeedbackRepo productFeedbackRepo;
    private final UserAccountRepo userAccountRepo;
    private final ProductRepo productRepo;
    private final OrderInvoiceDetailsRepo orderInvoiceDetailsRepo;
    private final service.sllbackend.utils.BadWordFilter badWordFilter;

    @Override
    @Transactional(readOnly = true)
    public boolean canUserRateProduct(String username, Integer productId) {
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElse(null);
        
        if (userAccount == null) {
            return false;
        }
        
        Product product = productRepo.findById(productId)
                .orElse(null);
        
        if (product == null) {
            return false;
        }
        
        // Check if user has any delivered order containing this product (optimized single query)
        return orderInvoiceDetailsRepo.existsByUserAccountAndProductIdAndOrderStatus(
                userAccount, productId, OrderStatus.DELIVERED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductFeedback> getProductFeedback(Integer productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        List<ProductFeedback> allFeedback = productFeedbackRepo.findByProductWithImagesOrderByIdDesc(product);
        
        // Filter out feedback containing bad words
        return allFeedback.stream()
                .filter(feedback -> {
                    String comment = feedback.getComment();
                    return comment == null || !badWordFilter.containsBadWord(comment);
                })
                .toList();
    }

    @Override
    @Transactional
    public ProductFeedback submitFeedback(String username, Integer productId, Short rating, String comment) {
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        // Check if user can rate this product
        if (!canUserRateProduct(username, productId)) {
            throw new IllegalArgumentException("You can only rate products from your completed orders");
        }
        
        // Check if user has violated bad word policy in their previous reviews
        if (hasUserViolatedBadWordPolicy(username)) {
            throw new IllegalArgumentException("You cannot submit feedback due to previous violations of our content policy");
        }
        
        // Note: We allow saving feedback with bad words to the database
        // They will be filtered out when loading to frontend in getProductFeedback()
        // But they are saved to track user violations and prevent future submissions
        
        // Check if user has already rated this product
        ProductFeedback existingFeedback = productFeedbackRepo
                .findByUserAccountAndProduct(userAccount, product)
                .orElse(null);
        
        if (existingFeedback != null) {
            // Update existing feedback
            existingFeedback.setRating(rating);
            existingFeedback.setComment(comment);
            return productFeedbackRepo.save(existingFeedback);
        } else {
            // Create new feedback
            ProductFeedback feedback = ProductFeedback.builder()
                    .userAccount(userAccount)
                    .product(product)
                    .rating(rating)
                    .comment(comment)
                    .build();
            return productFeedbackRepo.save(feedback);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserRatedProduct(String username, Integer productId) {
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElse(null);
        
        if (userAccount == null) {
            return false;
        }
        
        Product product = productRepo.findById(productId)
                .orElse(null);
        
        if (product == null) {
            return false;
        }
        
        return productFeedbackRepo.existsByUserAccountAndProduct(userAccount, product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductFeedback getUserFeedback(String username, Integer productId) {
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        return productFeedbackRepo.findByUserAccountAndProduct(userAccount, product)
                .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserViolatedBadWordPolicy(String username) {
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElse(null);
        
        if (userAccount == null) {
            return false;
        }
        
        // Get all feedback by this user
        List<ProductFeedback> userFeedback = productFeedbackRepo.findByUserAccount(userAccount);
        
        // Check if any feedback contains bad words
        return userFeedback.stream()
                .anyMatch(feedback -> {
                    String comment = feedback.getComment();
                    return comment != null && badWordFilter.containsBadWord(comment);
                });
    }
}
