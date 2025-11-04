package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.OrderStatus;
import service.sllbackend.repository.*;
import service.sllbackend.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderInvoiceRepo orderInvoiceRepo;
    private final OrderInvoiceDetailsRepo orderInvoiceDetailsRepo;
    private final CustomerInfoRepo customerInfoRepo;
    private final CartRepo cartRepo;
    private final UserAccountRepo userAccountRepo;
    private final VietQrServiceImpl vietQrService;

    @Override
    @Transactional
    public OrderInvoice placeOrder(String username, String customerName, String phoneNumber, 
                                  String shippingAddress, String paymentTypeName) {
        // Get user account
        UserAccount userAccount = userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get cart items
        List<Cart> cartItems = cartRepo.findByUserAccount(userAccount);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Calculate total price
        int totalPrice = cartItems.stream()
                .mapToInt(item -> item.getProduct().getCurrentPrice() * item.getAmount())
                .sum();
        
        // Get or create customer info
        CustomerInfo customerInfo = customerInfoRepo
                .findByPhoneNumberAndShippingAddress(phoneNumber, shippingAddress)
                .orElseGet(() -> customerInfoRepo.save(CustomerInfo.builder()
                        .name(customerName)
                        .phoneNumber(phoneNumber)
                        .shippingAddress(shippingAddress)
                        .build()));
        
        // Validate payment method (simple text validation)
        if (!paymentTypeName.equals("BANK_TRANSFER") && !paymentTypeName.equals("COD")) {
            throw new RuntimeException("Invalid payment method. Must be BANK_TRANSFER or COD");
        }
        
        // Create order invoice
        OrderInvoice orderInvoice = OrderInvoice.builder()
                .userAccount(userAccount)
                .customerInfo(customerInfo)
                .totalPrice(totalPrice)
                .paymentMethod(paymentTypeName)
                .orderStatus(OrderStatus.PENDING)
                .build();
        orderInvoice = orderInvoiceRepo.save(orderInvoice);
        
        // Generate QR code URL if payment method is bank transfer
        if ("BANK_TRANSFER".equals(paymentTypeName)) {
            String qrUrl = vietQrService.generateQrUrl(orderInvoice.getId(), username, totalPrice);
            orderInvoice.setPaymentQrUrl(qrUrl);
            orderInvoice = orderInvoiceRepo.save(orderInvoice);
        }
        
        // Create order details
        for (Cart cartItem : cartItems) {
            OrderInvoiceDetails details = OrderInvoiceDetails.builder()
                    .orderInvoice(orderInvoice)
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getAmount())
                    .priceAtSale(cartItem.getProduct().getCurrentPrice())
                    .build();
            orderInvoiceDetailsRepo.save(details);
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
}
