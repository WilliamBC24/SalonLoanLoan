package service.sllbackend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.DiscountType;
import service.sllbackend.enumerator.FulfillmentType;
import service.sllbackend.enumerator.OrderStatus;
import service.sllbackend.repository.*;
import service.sllbackend.service.InventoryService;
import service.sllbackend.service.OrderService;
import service.sllbackend.web.dto.InStoreOrderItemDTO;

import java.time.LocalDateTime;
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
    private final ObjectMapper objectMapper;
    private final ProductRepo productRepo;
    private final VoucherRedemptionRepo voucherRedemptionRepo;
    private final VoucherRepo voucherRepo;

    @Override
    @Transactional
    public OrderInvoice placeOrder(String username, String customerName, String phoneNumber,
                                   String shippingAddress, String city, String ward,
                                   String paymentTypeName, FulfillmentType fulfillmentType,
                                   String voucherCode) {

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

        // Validate delivery requirements and calculate shipping fee
        int shippingFee = 0;
        if (fulfillmentType == FulfillmentType.DELIVERY) {
            if (shippingAddress == null || shippingAddress.trim().isEmpty()) {
                throw new RuntimeException("Shipping address is required for delivery orders");
            }
            if (city == null || city.trim().isEmpty()) {
                throw new RuntimeException("City is required for delivery orders");
            }

            String normalizedCity = city.trim().toLowerCase();
            if (normalizedCity.equals("hanoi") || normalizedCity.equals("hà nội") || normalizedCity.equals("ha noi")) {
                shippingFee = HANOI_SHIPPING_FEE;
            } else {
                shippingFee = OTHER_CITIES_SHIPPING_FEE;
            }
        }

        // Validate payment method
        if (fulfillmentType == FulfillmentType.IN_STORE_PICKUP) {
            if (!paymentTypeName.equals("BANK_TRANSFER") && !paymentTypeName.equals("IN_STORE")) {
                throw new RuntimeException("Invalid payment method for in-store pickup. Must be BANK_TRANSFER or IN_STORE");
            }
        } else {
            if (!paymentTypeName.equals("BANK_TRANSFER") && !paymentTypeName.equals("COD")) {
                throw new RuntimeException("Invalid payment method. Must be BANK_TRANSFER or COD");
            }
        }

        // ===== Voucher logic =====
        Voucher appliedVoucher = null;
        int discountAmount = 0;

        if (voucherCode != null && !voucherCode.trim().isEmpty()) {
            String code = voucherCode.trim();

            // Lock row to prevent oversubscribe of max usage under concurrency
            appliedVoucher = voucherRepo.findByVoucherCodeForUpdate(code)
                    .orElseThrow(() -> new RuntimeException("Invalid voucher code"));

            // Validate voucher time window
            LocalDateTime now = LocalDateTime.now();
            if (appliedVoucher.getEffectiveFrom() != null && now.isBefore(appliedVoucher.getEffectiveFrom())) {
                throw new RuntimeException("Voucher is not active yet");
            }
            if (appliedVoucher.getEffectiveTo() != null && now.isAfter(appliedVoucher.getEffectiveTo())) {
                throw new RuntimeException("Voucher has expired");
            }

            // Validate usage remaining
            if (appliedVoucher.getUsedCount() >= appliedVoucher.getMaxUsage()) {
                throw new RuntimeException("Voucher has reached maximum usage");
            }

            // Validate status (adjust this to your VoucherStatus structure)
            // Example assumes voucherStatus has a "code" field = "ACTIVE"
            if (appliedVoucher.getVoucherStatus() == null
                    || appliedVoucher.getVoucherStatus().getName() == null
                    || !appliedVoucher.getVoucherStatus().getName().equalsIgnoreCase("ACTIVE")) {
                throw new RuntimeException("Voucher is not available");
            }

            // Compute discount based on type
            if (appliedVoucher.getDiscountType() == DiscountType.PERCENTAGE) {
                int pct = appliedVoucher.getDiscountAmount(); // 1..100
                discountAmount = (int) Math.floor(subtotal * (pct / 100.0));
            } else { // AMOUNT
                discountAmount = appliedVoucher.getDiscountAmount();
            }

            // Clamp discount to [0..subtotal]
            if (discountAmount < 0) discountAmount = 0;
            if (discountAmount > subtotal) discountAmount = subtotal;
        }

        // Total after discount + shipping
        int totalPrice = (subtotal - discountAmount) + shippingFee;

        // Ensure DB check total_price > 0 passes
        if (totalPrice <= 0) {
            throw new RuntimeException("Total price must be greater than 0 after applying voucher");
        }

        // Create customer info snapshot
        CustomerInfo customerInfo;
        if (fulfillmentType == FulfillmentType.DELIVERY) {
            customerInfo = customerInfoRepo.save(CustomerInfo.builder()
                    .name(customerName)
                    .phoneNumber(phoneNumber)
                    .shippingAddress(shippingAddress)
                    .city(city)
                    .ward(ward)
                    .build());
        } else {
            customerInfo = customerInfoRepo.save(CustomerInfo.builder()
                    .name(customerName)
                    .phoneNumber(phoneNumber)
                    .build());
        }

        // Create order invoice (attach voucher)
        OrderInvoice orderInvoice = OrderInvoice.builder()
                .userAccount(userAccount)
                .customerInfo(customerInfo)
                .appliedVoucher(appliedVoucher)         // ✅ store voucher FK on order
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

            inventoryService.reduceStock(
                    cartItem.getProduct().getId(),
                    cartItem.getAmount(),
                    orderInvoice.getId()
            );
        }

        // If voucher applied -> increment used_count + create redemption record
        if (appliedVoucher != null) {
            appliedVoucher.setUsedCount(appliedVoucher.getUsedCount() + 1);
            voucherRepo.save(appliedVoucher);

            voucherRedemptionRepo.save(VoucherRedemption.builder()
                    .voucher(appliedVoucher)
                    .userAccount(userAccount)
                    .redeemedDate(LocalDateTime.now())
                    .build());
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

    @Transactional
    public OrderInvoice createInStoreOrder(
            String staffUsername,       // staff who creates the order
            String username,            // optional assigned user (customer username)
            String customerName,
            String phoneNumber,
            String paymentTypeName,     // must be IN_STORE or BANK_TRANSFER
            String itemsJson           // JSON list of items: [{productId, quantity}, ...]
    ) {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new RuntimeException("Customer name is required");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new RuntimeException("Phone number is required");
        }

        // Payment validation: in-store pickup only allows IN_STORE or BANK_TRANSFER
        if (!"IN_STORE".equals(paymentTypeName) && !"BANK_TRANSFER".equals(paymentTypeName)) {
            throw new RuntimeException("Invalid payment method for in-store order. Must be IN_STORE or BANK_TRANSFER");
        }

        // Determine which user account will be linked to the order
        // - If username (assigned user) is provided, use that
        // - Otherwise, fall back to staff account (cannot be null because user_account_id is NOT NULL)
        String effectiveUsername = null;
        if (username != null && !username.trim().isEmpty()) {
            effectiveUsername = username.trim();
        } else if (staffUsername != null && !staffUsername.trim().isEmpty()) {
            effectiveUsername = staffUsername.trim();
        }

        if (effectiveUsername == null) {
            throw new RuntimeException("No user account available to attach this order");
        }
        final String finalEffectiveName = effectiveUsername;

        UserAccount userAccount = userAccountRepo.findByUsername(finalEffectiveName)
                .orElse(userAccountRepo.findByUsername("anon").get());

        // Parse itemsJson -> list of items
        List<InStoreOrderItemDTO> items;
        try {
            items = objectMapper.readValue(
                    itemsJson,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, InStoreOrderItemDTO.class)
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid items data", e);
        }

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Order must contain at least one product");
        }

        // Normalize + basic validation
        items = items.stream()
                .filter(i -> i.getProductId() != null && i.getQuantity() != null && i.getQuantity() > 0)
                .toList();

        if (items.isEmpty()) {
            throw new RuntimeException("All items have invalid quantity");
        }

        // Check stock for all items first
        for (InStoreOrderItemDTO item : items) {
            Integer productId = item.getProductId();
            Integer requiredQuantity = item.getQuantity();

            if (!inventoryService.hasEnoughStock(productId, requiredQuantity)) {
                Integer available = inventoryService.getAvailableStock(productId);
                Product p = productRepo.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                throw new RuntimeException(
                        "Insufficient stock for product: " + p.getProductName() +
                                ". Required: " + requiredQuantity +
                                ", Available: " + available
                );
            }
        }

        // Calculate subtotal using current product price from DB
        int subtotal = 0;
        Map<Integer, Product> productCache = new HashMap<>();

        for (InStoreOrderItemDTO item : items) {
            Product product = productCache.computeIfAbsent(
                    item.getProductId(),
                    id -> productRepo.findById(id)
                            .orElseThrow(() -> new RuntimeException("Product not found: " + id))
            );

            subtotal += product.getCurrentPrice() * item.getQuantity();
        }

        if (subtotal <= 0) {
            throw new RuntimeException("Order total must be greater than 0");
        }

        int shippingFee = 0; // in-store pickup only
        int totalPrice = subtotal + shippingFee;

        // Create CustomerInfo snapshot (no shipping address for in-store pickup)
        CustomerInfo customerInfo = customerInfoRepo.save(
                CustomerInfo.builder()
                        .name(customerName)
                        .phoneNumber(phoneNumber)
                        .build()
        );

        // Create OrderInvoice
        OrderInvoice orderInvoice = OrderInvoice.builder()
                .userAccount(userAccount)
                .customerInfo(customerInfo)
                .totalPrice(totalPrice)
                .shippingFee(shippingFee)
                .paymentMethod(paymentTypeName)
                .fulfillmentType(FulfillmentType.IN_STORE_PICKUP)
                .orderStatus(OrderStatus.PICKED_UP)
                .build();

        orderInvoice = orderInvoiceRepo.save(orderInvoice);

        // Create details + reduce stock
        for (InStoreOrderItemDTO item : items) {
            Product product = productCache.get(item.getProductId());

            OrderInvoiceDetails details = OrderInvoiceDetails.builder()
                    .orderInvoice(orderInvoice)
                    .product(product)
                    .quantity(item.getQuantity())
                    .priceAtSale(product.getCurrentPrice())
                    .build();

            orderInvoiceDetailsRepo.save(details);

            inventoryService.reduceStock(
                    product.getId(),
                    item.getQuantity(),
                    orderInvoice.getId()
            );
        }


        return orderInvoice;
    }

}
