package service.sllbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.FulfillmentType;
import service.sllbackend.enumerator.OrderStatus;
import service.sllbackend.repository.*;
import service.sllbackend.service.impl.OrderServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

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

    private UserAccount testUser;
    private Product testProduct;
    private Cart testCartItem;

    @BeforeEach
    void setUp() {
        testUser = UserAccount.builder()
                .id(1)
                .username("testuser")
                .email("test@example.com")
                .build();

        testProduct = Product.builder()
                .id(1)
                .productName("Test Product")
                .currentPrice(100000)
                .build();

        testCartItem = Cart.builder()
                .userAccount(testUser)
                .product(testProduct)
                .amount(2)
                .build();
    }

    @Test
    void testPlaceOrder_WithEmptyCart_ShouldThrowException() {
        // Arrange
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(cartRepo.findByUserAccount(testUser)).thenReturn(List.of());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.placeOrder("testuser", "John Doe", "0123456789",
                        "123 Main St", "Hanoi", "Ward 1", "BANK_TRANSFER", FulfillmentType.DELIVERY)
        );

        assertEquals("Cart is empty", exception.getMessage());
    }

    @Test
    void testPlaceOrder_WithInsufficientStock_ShouldThrowException() {
        // Arrange
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(cartRepo.findByUserAccount(testUser)).thenReturn(List.of(testCartItem));
        when(inventoryService.hasEnoughStock(1, 2)).thenReturn(false);
        when(inventoryService.getAvailableStock(1)).thenReturn(1);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.placeOrder("testuser", "John Doe", "0123456789",
                        "123 Main St", "Hanoi", "Ward 1", "BANK_TRANSFER", FulfillmentType.DELIVERY)
        );

        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    void testPlaceOrder_DeliveryWithoutAddress_ShouldThrowException() {
        // Arrange
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(cartRepo.findByUserAccount(testUser)).thenReturn(List.of(testCartItem));
        when(inventoryService.hasEnoughStock(1, 2)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.placeOrder("testuser", "John Doe", "0123456789",
                        null, "Hanoi", "Ward 1", "BANK_TRANSFER", FulfillmentType.DELIVERY)
        );

        assertEquals("Shipping address is required for delivery orders", exception.getMessage());
    }

    @Test
    void testPlaceOrder_DeliveryWithoutCity_ShouldThrowException() {
        // Arrange
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(cartRepo.findByUserAccount(testUser)).thenReturn(List.of(testCartItem));
        when(inventoryService.hasEnoughStock(1, 2)).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.placeOrder("testuser", "John Doe", "0123456789",
                        "123 Main St", null, "Ward 1", "BANK_TRANSFER", FulfillmentType.DELIVERY)
        );

        assertEquals("City is required for delivery orders", exception.getMessage());
    }

    @Test
    void testPlaceOrder_InStorePickupWithInvalidPayment_ShouldThrowException() {
        // Arrange
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(cartRepo.findByUserAccount(testUser)).thenReturn(List.of(testCartItem));
        when(inventoryService.hasEnoughStock(1, 2)).thenReturn(true);

        CustomerInfo customerInfo = CustomerInfo.builder()
                .id(1)
                .name("John Doe")
                .phoneNumber("0123456789")
                .build();
        when(customerInfoRepo.save(any(CustomerInfo.class))).thenReturn(customerInfo);

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.placeOrder("testuser", "John Doe", "0123456789",
                        null, null, null, "COD", FulfillmentType.IN_STORE_PICKUP)
        );

        assertTrue(exception.getMessage().contains("Invalid payment method for in-store pickup"));
    }

    @Test
    void testCancelOrder_WithUnauthorizedUser_ShouldThrowException() {
        // Arrange
        UserAccount orderOwner = UserAccount.builder()
                .id(1)
                .username("owner")
                .build();

        UserAccount differentUser = UserAccount.builder()
                .id(2)
                .username("different")
                .build();

        OrderInvoice order = OrderInvoice.builder()
                .id(1)
                .userAccount(orderOwner)
                .orderStatus(OrderStatus.PENDING)
                .build();

        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(order));
        when(userAccountRepo.findByUsername("different")).thenReturn(Optional.of(differentUser));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.cancelOrder(1, "different")
        );

        assertEquals("Unauthorized to cancel this order", exception.getMessage());
    }

    @Test
    void testCancelOrder_WithInvalidStatus_ShouldThrowException() {
        // Arrange
        OrderInvoice order = OrderInvoice.builder()
                .id(1)
                .userAccount(testUser)
                .orderStatus(OrderStatus.DELIVERED)
                .build();

        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(order));
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.cancelOrder(1, "testuser")
        );

        assertTrue(exception.getMessage().contains("Cannot cancel order"));
    }

    @Test
    void testCancelOrder_WithValidRequest_ShouldSucceed() {
        // Arrange
        OrderInvoice order = OrderInvoice.builder()
                .id(1)
                .userAccount(testUser)
                .orderStatus(OrderStatus.PENDING)
                .build();

        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(order));
        when(userAccountRepo.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        doNothing().when(inventoryService).returnStock(1);
        when(orderInvoiceRepo.save(any(OrderInvoice.class))).thenReturn(order);

        // Act
        assertDoesNotThrow(() -> orderService.cancelOrder(1, "testuser"));

        // Assert
        verify(inventoryService).returnStock(1);
        verify(orderInvoiceRepo).save(order);
    }

    @Test
    void testGetOrderDetails_WithValidId_ShouldReturnOrder() {
        // Arrange
        OrderInvoice order = OrderInvoice.builder()
                .id(1)
                .userAccount(testUser)
                .orderStatus(OrderStatus.PENDING)
                .build();

        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(order));

        // Act
        OrderInvoice result = orderService.getOrderDetails(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(orderInvoiceRepo).findById(1);
    }

    @Test
    void testUpdateOrderStatus_ShouldUpdateStatus() {
        // Arrange
        OrderInvoice order = OrderInvoice.builder()
                .id(1)
                .userAccount(testUser)
                .orderStatus(OrderStatus.PENDING)
                .build();

        when(orderInvoiceRepo.findById(1)).thenReturn(Optional.of(order));
        when(orderInvoiceRepo.save(any(OrderInvoice.class))).thenReturn(order);

        // Act
        orderService.updateOrderStatus(1, OrderStatus.CONFIRMED);

        // Assert
        verify(orderInvoiceRepo).save(order);
        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
    }

    @Test
    void testCountByUser_ShouldReturnCount() {
        // Arrange
        when(orderInvoiceRepo.countByUserAccount(testUser)).thenReturn(5L);

        // Act
        long count = orderService.countByUser(testUser);

        // Assert
        assertEquals(5L, count);
        verify(orderInvoiceRepo).countByUserAccount(testUser);
    }
}
