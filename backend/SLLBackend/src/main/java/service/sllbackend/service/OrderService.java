package service.sllbackend.service;

import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.entity.OrderInvoiceDetails;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.FulfillmentType;
import service.sllbackend.enumerator.OrderStatus;

import java.util.List;
import java.util.Map;

public interface OrderService {
    

    OrderInvoice placeOrder(String username, String customerName, String phoneNumber, 
                           String shippingAddress, String city, String ward,
                           String paymentTypeName, FulfillmentType fulfillmentType, String voucherCode);
    
    List<OrderInvoice> getOrderHistory(String username);
    
    OrderInvoice getOrderDetails(Integer orderId, String username);
    
    /**
     * Get order items/details
     * @param orderId Order ID
     * @param username User's username for verification
     * @return List of order items
     */
    List<OrderInvoiceDetails> getOrderItems(Integer orderId, String username);
    
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
    
    /**
     * Get order details for staff/admin (no ownership verification)
     * @param orderId Order ID
     * @return Order invoice with details
     */
    OrderInvoice getOrderDetailsForStaff(Integer orderId);

    long countByUser(UserAccount user);

    OrderInvoice createInStoreOrder(
            String staffUsername,       // staff who creates the order
            String username,            // optional assigned user (customer username)
            String customerName,
            String phoneNumber,
            String paymentTypeName,     // must be IN_STORE or BANK_TRANSFER
            String itemsJson    // JSON list of items: [{productId, quantity}, ...]
    );
}
