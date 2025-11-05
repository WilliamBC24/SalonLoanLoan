package service.sllbackend.service;

import service.sllbackend.entity.ProductFeedback;

import java.util.List;

public interface ProductFeedbackService {
    
    /**
     * Check if a user can rate a product (has a DELIVERED order with the product)
     * @param username User's username
     * @param productId Product ID
     * @return true if user can rate, false otherwise
     */
    boolean canUserRateProduct(String username, Integer productId);
    
    /**
     * Get all feedback for a product
     * @param productId Product ID
     * @return List of product feedback
     */
    List<ProductFeedback> getProductFeedback(Integer productId);
    
    /**
     * Submit or update product feedback
     * @param username User's username
     * @param productId Product ID
     * @param rating Rating (1-5)
     * @param comment Optional comment
     * @return Created or updated product feedback
     */
    ProductFeedback submitFeedback(String username, Integer productId, Short rating, String comment);
    
    /**
     * Check if user has already rated a product
     * @param username User's username
     * @param productId Product ID
     * @return true if user has rated, false otherwise
     */
    boolean hasUserRatedProduct(String username, Integer productId);
    
    /**
     * Get user's existing feedback for a product
     * @param username User's username
     * @param productId Product ID
     * @return User's product feedback if exists
     */
    ProductFeedback getUserFeedback(String username, Integer productId);
}
