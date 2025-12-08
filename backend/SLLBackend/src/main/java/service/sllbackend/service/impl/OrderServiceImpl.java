package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.FulfillmentType;
import service.sllbackend.enumerator.OrderStatus;
import service.sllbackend.repository.*;
import service.sllbackend.service.InventoryService;
import service.sllbackend.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    // Shipping fee constants (in VND)
    private static final int HANOI_SHIPPING_FEE = 30000;
    private static final int OTHER_CITIES_SHIPPING_FEE = 70000;
    private static final String HANOI_CITY_NAME = "Hanoi";
    
    private final OrderInvoiceRepo orderInvoiceRepo;
    private final OrderInvoiceDetailsRepo orderInvoiceDetailsRepo;
    private final CustomerInfoRepo customerInfoRepo;
    private final CartRepo cartRepo;
    private final UserAccountRepo userAccountRepo;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public OrderInvoice placeOrder(String username, String customerName, String phoneNumber, 
                                  String shippingAddress, String city, String ward,
                                  String paymentTypeName, FulfillmentType fulfillmentType) {
        // Get user account
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get cart items
        List<Cart> cartItems = cartRepo.findByUserAccount(userAccount);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Check stock availability for all items first
        for (Cart cartItem : cartItems) {
            Integer productId = cartItem.getProduct().getId();
            Integer requiredQuantity = cartItem.getAmount();
            
            if (!inventoryService.hasEnoughStock(productId, requiredQuantity)) {
                Integer availableStock = inventoryService.getAvailableStock(productId);
                throw new RuntimeException("Insufficient stock for product: " + 
                        cartItem.getProduct().getProductName() + 
                        ". Required: " + requiredQuantity + ", Available: " + availableStock);
            }
        }
        
        // Calculate subtotal (product prices only)
        int subtotal = cartItems.stream()
                .mapToInt(item -> item.getProduct().getCurrentPrice() * item.getAmount())
                .sum();
        
        // Calculate shipping fee based on city
        int shippingFee = 0;
        if (fulfillmentType == FulfillmentType.DELIVERY) {
            // Shipping fee: 30,000 for Hanoi, 70,000 for all other cities
            if (city != null && city.trim().equalsIgnoreCase(HANOI_CITY_NAME)) {
                shippingFee = HANOI_SHIPPING_FEE;
            } else if (city != null && !city.trim().isEmpty()) {
                shippingFee = OTHER_CITIES_SHIPPING_FEE;
            }
        }
        
        // Calculate total price (subtotal + shipping fee)
        int totalPrice = subtotal + shippingFee;
        
        // Create customer info for this order
        // Note: Each order creates a new CustomerInfo record to maintain a snapshot of the
        // delivery details at the time of order. This allows tracking different addresses
        // used by the same customer over time and preserves historical order information.
        CustomerInfo customerInfo;
        if (fulfillmentType == FulfillmentType.DELIVERY) {
            // For delivery, require shipping address and city
            if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
                throw new RuntimeException("Shipping address is required for delivery orders");
            }
            if (city == null || city.trim().isEmpty()) {
                throw new RuntimeException("City is required for delivery orders");
            }
            customerInfo = customerInfoRepo.save(CustomerInfo.builder()
                    .name(customerName)
                    .phoneNumber(phoneNumber)
                    .shippingAddress(shippingAddress)
                    .city(city)
                    .ward(ward)
                    .build());
        } else {
            // For in-store pickup, create customer info without shipping address
            customerInfo = customerInfoRepo.save(CustomerInfo.builder()
                    .name(customerName)
                    .phoneNumber(phoneNumber)
                    .build());
        }
        
        // Validate payment method
        // For in-store pickup, only allow BANK_TRANSFER or IN_STORE (pay at pickup)
        if (fulfillmentType == FulfillmentType.IN_STORE_PICKUP) {
            if (!paymentTypeName.equals("BANK_TRANSFER") && !paymentTypeName.equals("IN_STORE")) {
                throw new RuntimeException("Invalid payment method for in-store pickup. Must be BANK_TRANSFER or IN_STORE");
            }
        } else {
            if (!paymentTypeName.equals("BANK_TRANSFER") && !paymentTypeName.equals("COD")) {
                throw new RuntimeException("Invalid payment method. Must be BANK_TRANSFER or COD");
            }
        }
        
        // Create order invoice
        OrderInvoice orderInvoice = OrderInvoice.builder()
                .userAccount(userAccount)
                .customerInfo(customerInfo)
                .totalPrice(totalPrice)
                .shippingFee(shippingFee)
                .paymentMethod(paymentTypeName)
                .fulfillmentType(fulfillmentType)
                .orderStatus(OrderStatus.PENDING)
                .build();
        orderInvoice = orderInvoiceRepo.save(orderInvoice);
        
        // Create order details and reduce stock
        for (Cart cartItem : cartItems) {
            OrderInvoiceDetails details = OrderInvoiceDetails.builder()
                    .orderInvoice(orderInvoice)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getAmount())
                    .priceAtSale(cartItem.getProduct().getCurrentPrice())
                    .build();
            orderInvoiceDetailsRepo.save(details);
            
            // Reduce stock for this product
            inventoryService.reduceStock(
                    cartItem.getProduct().getId(), 
                    cartItem.getAmount(),
                    orderInvoice.getId()
            );
        }
        
        // Clear cart
        cartRepo.deleteAll(cartItems);
        
        return orderInvoice;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderInvoice> getOrderHistory(String username) {
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return orderInvoiceRepo.findByUserAccountOrderByCreatedAtDesc(userAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderInvoice getOrderDetails(Integer orderId) {
        return orderInvoiceRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderInvoiceDetails> getOrderItems(Integer orderId) {
        OrderInvoice orderInvoice = getOrderDetails(orderId);
        return orderInvoiceDetailsRepo.findByOrderInvoice(orderInvoice);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId, String username) {
        OrderInvoice order = getOrderDetails(orderId);
        
        // Verify user owns this order
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!order.getUserAccount().getId().equals(userAccount.getId())) {
            throw new RuntimeException("Unauthorized to cancel this order");
        }
        
        // Only allow cancellation of pending or confirmed orders
        if (order.getOrderStatus() != OrderStatus.PENDING && 
            order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new RuntimeException("Cannot cancel order in " + order.getOrderStatus() + " status");
        }
        
        // Return stock before marking as cancelled
        inventoryService.returnStock(orderId);
        
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderInvoiceRepo.save(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        OrderInvoice order = getOrderDetails(orderId);
        order.setOrderStatus(newStatus);
        orderInvoiceRepo.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCartSummary(String username) {
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Cart> cartItems = cartRepo.findByUserAccount(userAccount);
        
        int totalPrice = cartItems.stream()
                .mapToInt(item -> item.getProduct().getCurrentPrice() * item.getAmount())
                .sum();
        
        int itemCount = cartItems.stream()
                .mapToInt(Cart::getAmount)
                .sum();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalPrice", totalPrice);
        summary.put("itemCount", itemCount);
        summary.put("items", cartItems);
        
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderInvoice> getAllOrders() {
        return orderInvoiceRepo.findAll();
    }

    @Override
    public long countByUser(UserAccount user) {
        return orderInvoiceRepo.countByUserAccount(user);
    }
}
