package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.InventoryTransactionReason;
import service.sllbackend.enumerator.InventoryTransactionType;
import service.sllbackend.repository.*;
import service.sllbackend.service.InventoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    
    private final InventoryLotRepo inventoryLotRepo;
    private final InventoryTransactionRepo inventoryTransactionRepo;
    private final OrderInvoiceDetailsRepo orderInvoiceDetailsRepo;
    private final StaffRepo staffRepo;
    
    // Map to track which lots were used for which order
    // Key: orderId, Value: Map of lotId -> quantity taken
    private final Map<Integer, Map<Integer, Integer>> orderLotTracking = new HashMap<>();
    
    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableStock(Integer productId) {
        Integer stock = inventoryLotRepo.getTotalAvailableStock(productId);
        return stock != null ? stock : 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasEnoughStock(Integer productId, Integer requiredQuantity) {
        Integer availableStock = getAvailableStock(productId);
        return availableStock >= requiredQuantity;
    }
    
    @Override
    @Transactional
    public void reduceStock(Integer productId, Integer quantity, Integer orderId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        // Get available lots ordered by expiry date (FIFO)
        List<InventoryLot> availableLots = inventoryLotRepo
                .findAvailableLotsByProductOrderByExpiryDate(productId);
        
        if (availableLots.isEmpty()) {
            throw new RuntimeException("No stock available for product ID: " + productId);
        }
        
        int remainingQuantity = quantity;
        Map<Integer, Integer> lotsUsed = new HashMap<>();
        
        // Get first staff (admin) for transaction record
        Staff staff = staffRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No staff found to record transaction"));
        
        for (InventoryLot lot : availableLots) {
            if (remainingQuantity <= 0) break;
            
            int quantityFromThisLot = Math.min(remainingQuantity, lot.getAvailableQuantity());
            
            // Reduce the lot quantity
            lot.setAvailableQuantity(lot.getAvailableQuantity() - quantityFromThisLot);
            inventoryLotRepo.save(lot);
            
            // Record the transaction
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .inventoryLot(lot)
                    .staff(staff)
                    .transactionType(InventoryTransactionType.OUT)
                    .quantity(quantityFromThisLot)
                    .reason(InventoryTransactionReason.SERVICE)
                    .build();
            inventoryTransactionRepo.save(transaction);
            
            // Track which lot we took from
            lotsUsed.put(lot.getId(), quantityFromThisLot);
            
            remainingQuantity -= quantityFromThisLot;
            
            log.info("Reduced {} units from lot {} for product {}", 
                    quantityFromThisLot, lot.getId(), productId);
        }
        
        if (remainingQuantity > 0) {
            throw new RuntimeException("Insufficient stock for product ID: " + productId + 
                    ". Required: " + quantity + ", Available: " + (quantity - remainingQuantity));
        }
        
        // Track the lots used for this order for potential rollback
        orderLotTracking.put(orderId, lotsUsed);
        
        log.info("Successfully reduced {} units for product {} (order {})", 
                quantity, productId, orderId);
    }
    
    @Override
    @Transactional
    public void returnStock(Integer orderId) {
        // Get order details to find what products need to be returned
        List<OrderInvoiceDetails> orderDetails = orderInvoiceDetailsRepo
                .findByOrderInvoice_Id(orderId);
        
        if (orderDetails.isEmpty()) {
            log.warn("No order details found for order {}", orderId);
            return;
        }
        
        // Get first staff (admin) for transaction record
        Staff staff = staffRepo.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No staff found to record transaction"));
        
        // For each product in the order, find recent lots and return stock
        for (OrderInvoiceDetails detail : orderDetails) {
            Integer productId = detail.getProduct().getId();
            Integer quantity = detail.getQuantity();
            
            // Get lots for this product (prefer newer lots for returns)
            List<InventoryLot> lots = inventoryLotRepo
                    .findAvailableLotsByProductOrderByExpiryDate(productId);
            
            if (lots.isEmpty()) {
                log.warn("No lots found for product {} to return stock", productId);
                continue;
            }
            
            // Return to the first available lot (could be enhanced to track exact lots)
            InventoryLot lot = lots.get(0);
            lot.setAvailableQuantity(lot.getAvailableQuantity() + quantity);
            inventoryLotRepo.save(lot);
            
            // Record the transaction
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .inventoryLot(lot)
                    .staff(staff)
                    .transactionType(InventoryTransactionType.IN)
                    .quantity(quantity)
                    .reason(InventoryTransactionReason.SERVICE)
                    .build();
            inventoryTransactionRepo.save(transaction);
            
            log.info("Returned {} units to lot {} for product {} (order cancelled: {})", 
                    quantity, lot.getId(), productId, orderId);
        }
        
        // Clean up tracking
        orderLotTracking.remove(orderId);
        
        log.info("Successfully returned stock for cancelled order {}", orderId);
    }
}
