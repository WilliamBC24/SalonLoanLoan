package service.sllbackend.service;

public interface InventoryService {
    
    /**
     * Get the total available stock for a product
     */
    Integer getAvailableStock(Integer productId);
    
    /**
     * Check if there's enough stock for a product
     */
    boolean hasEnoughStock(Integer productId, Integer requiredQuantity);
    
    /**
     * Reduce stock when order is placed (FIFO - First In First Out based on expiry date)
     */
    void reduceStock(Integer productId, Integer quantity, Integer orderId);
    
    /**
     * Return stock when order is cancelled
     */
    void returnStock(Integer orderId);
}
