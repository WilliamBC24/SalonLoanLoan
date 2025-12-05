package service.sllbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.OrderStatus;
import service.sllbackend.repository.*;
import service.sllbackend.service.impl.OrderServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderInvoiceRepo orderInvoiceRepo;

    @Mock
    private OrderInvoiceDetailsRepo orderInvoiceDetailsRepo;

    @Mock
    private CustomerInfoRepo customerInfoRepo;

    @Mock
    private CartRepo cartRepo;

    @Mock
    private UserAccountRepo userAccountRepo;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderInvoice testOrder;
    private UserAccount testUser;

    @BeforeEach
    void setUp() {
        testUser = UserAccount.builder()
                .id(1)
                .username("testuser")
                .build();

        testOrder = OrderInvoice.builder()
                .id(1)
                .userAccount(testUser)
                .totalPrice(100000)
                .paymentMethod("BANK_TRANSFER")
                .orderStatus(OrderStatus.PENDING)
                .build();
    }

    @Test
    void updateOrderStatus_FromPending_ShouldSucceed() {
        testOrder.setOrderStatus(OrderStatus.PENDING);
        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(testOrder));
        when(orderInvoiceRepo.save(any(OrderInvoice.class))).thenReturn(testOrder);

        assertDoesNotThrow(() -> orderService.updateOrderStatus(1, OrderStatus.CONFIRMED));
        
        assertEquals(OrderStatus.CONFIRMED, testOrder.getOrderStatus());
        verify(orderInvoiceRepo).save(testOrder);
    }

    @Test
    void updateOrderStatus_FromConfirmed_ShouldSucceed() {
        testOrder.setOrderStatus(OrderStatus.CONFIRMED);
        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(testOrder));
        when(orderInvoiceRepo.save(any(OrderInvoice.class))).thenReturn(testOrder);

        assertDoesNotThrow(() -> orderService.updateOrderStatus(1, OrderStatus.PROCESSING));
        
        assertEquals(OrderStatus.PROCESSING, testOrder.getOrderStatus());
        verify(orderInvoiceRepo).save(testOrder);
    }

    @Test
    void updateOrderStatus_FromCancelled_ShouldThrowException() {
        testOrder.setOrderStatus(OrderStatus.CANCELLED);
        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(testOrder));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(1, OrderStatus.PENDING)
        );

        assertTrue(exception.getMessage().contains("Cannot change order status from CANCELLED"));
        assertTrue(exception.getMessage().contains("final and irreversible"));
        verify(orderInvoiceRepo, never()).save(any());
    }

    @Test
    void updateOrderStatus_FromDelivered_ShouldThrowException() {
        testOrder.setOrderStatus(OrderStatus.DELIVERED);
        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(testOrder));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(1, OrderStatus.PENDING)
        );

        assertTrue(exception.getMessage().contains("Cannot change order status from DELIVERED"));
        assertTrue(exception.getMessage().contains("final and irreversible"));
        verify(orderInvoiceRepo, never()).save(any());
    }

    @Test
    void updateOrderStatus_FromCancelledToConfirmed_ShouldThrowException() {
        testOrder.setOrderStatus(OrderStatus.CANCELLED);
        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(testOrder));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(1, OrderStatus.CONFIRMED)
        );

        assertTrue(exception.getMessage().contains("CANCELLED"));
        verify(orderInvoiceRepo, never()).save(any());
    }

    @Test
    void updateOrderStatus_FromDeliveredToShipped_ShouldThrowException() {
        testOrder.setOrderStatus(OrderStatus.DELIVERED);
        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(testOrder));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> orderService.updateOrderStatus(1, OrderStatus.SHIPPED)
        );

        assertTrue(exception.getMessage().contains("DELIVERED"));
        verify(orderInvoiceRepo, never()).save(any());
    }

    @Test
    void updateOrderStatus_FromProcessingToShipped_ShouldSucceed() {
        testOrder.setOrderStatus(OrderStatus.PROCESSING);
        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(testOrder));
        when(orderInvoiceRepo.save(any(OrderInvoice.class))).thenReturn(testOrder);

        assertDoesNotThrow(() -> orderService.updateOrderStatus(1, OrderStatus.SHIPPED));
        
        assertEquals(OrderStatus.SHIPPED, testOrder.getOrderStatus());
        verify(orderInvoiceRepo).save(testOrder);
    }

    @Test
    void updateOrderStatus_FromShippedToDelivered_ShouldSucceed() {
        testOrder.setOrderStatus(OrderStatus.SHIPPED);
        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(testOrder));
        when(orderInvoiceRepo.save(any(OrderInvoice.class))).thenReturn(testOrder);

        assertDoesNotThrow(() -> orderService.updateOrderStatus(1, OrderStatus.DELIVERED));
        
        assertEquals(OrderStatus.DELIVERED, testOrder.getOrderStatus());
        verify(orderInvoiceRepo).save(testOrder);
    }
}
