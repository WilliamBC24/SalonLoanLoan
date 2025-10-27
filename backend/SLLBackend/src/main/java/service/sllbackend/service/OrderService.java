package service.sllbackend.service;

import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.entity.OrderInvoiceDetails;
import service.sllbackend.enumerator.OrderStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {
    
    /**
     * Place an order from user's cart
     * @param username User's username
     * @param customerName Customer's name
     * @param phoneNumber Delivery phone number
     * @param shippingAddress Delivery address
     * @param paymentTypeName Payment method name (e.g., "BANK_TRANSFER", "COD")
     * @return Created order invoice
     */
    OrderInvoice placeOrder(String username, String customerName, String phoneNumber, 
                           String shippingAddress, String paymentTypeName);
    
    /**
     * Get order history for a user
     * @param username User's username
     * @return List of order invoices
     */
    List<OrderInvoice> getOrderHistory(String username);
    
    /**
     * Get order details by ID
     * @param orderId Order ID
     * @return Order invoice with details
     */
    OrderInvoice getOrderDetails(Integer orderId);
    
    /**
     * Get order items/details
     * @param orderId Order ID
     * @return List of order items
     */
    List<OrderInvoiceDetails> getOrderItems(Integer orderId);
    
    /**
     * Cancel an order
     * @param orderId Order ID
     * @param username User's username for verification
     */
    void cancelOrder(Integer orderId, String username);
    
    /**
     * Update order status (admin/staff only)
     * @param orderId Order ID
     * @param newStatus New order status
     */
    void updateOrderStatus(Integer orderId, OrderStatus newStatus);
    
    /**
     * Calculate cart summary
     * @param username User's username
     * @return Map with cart summary info (total, itemCount)
     */
    Map<String, Object> getCartSummary(String username);
    
    /**
     * Get all orders (for staff/admin)
     * @return List of all order invoices
     */
    List<OrderInvoice> getAllOrders();
}
