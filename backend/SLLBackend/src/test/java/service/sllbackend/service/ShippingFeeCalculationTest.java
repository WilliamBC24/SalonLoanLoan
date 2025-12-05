package service.sllbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.repository.*;
import service.sllbackend.service.impl.OrderServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShippingFeeCalculationTest {
    
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
    
    private OrderServiceImpl orderService;
    
    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(
                orderInvoiceRepo,
                orderInvoiceDetailsRepo,
                customerInfoRepo,
                cartRepo,
                userAccountRepo,
                inventoryService
        );
    }
    
    @Test
    void testCalculateShippingFee_Hanoi() {
        // Hanoi in Vietnamese
        assertEquals(30000, orderService.calculateShippingFee("Hà Nội"));
        
        // Hanoi in English
        assertEquals(30000, orderService.calculateShippingFee("Hanoi"));
        
        // Case insensitive
        assertEquals(30000, orderService.calculateShippingFee("HANOI"));
        assertEquals(30000, orderService.calculateShippingFee("hà nội"));
        assertEquals(30000, orderService.calculateShippingFee("Ha Noi"));
        assertEquals(30000, orderService.calculateShippingFee("HA NOI"));
    }
    
    @Test
    void testCalculateShippingFee_OtherCities() {
        // Ho Chi Minh City
        assertEquals(70000, orderService.calculateShippingFee("Hồ Chí Minh"));
        
        // Da Nang
        assertEquals(70000, orderService.calculateShippingFee("Đà Nẵng"));
        
        // Hai Phong
        assertEquals(70000, orderService.calculateShippingFee("Hải Phòng"));
        
        // Can Tho
        assertEquals(70000, orderService.calculateShippingFee("Cần Thơ"));
        
        // Other cities
        assertEquals(70000, orderService.calculateShippingFee("Bình Dương"));
        assertEquals(70000, orderService.calculateShippingFee("Long An"));
        assertEquals(70000, orderService.calculateShippingFee("Đồng Nai"));
    }
    
    @Test
    void testCalculateShippingFee_NullCity() {
        // Null city should return default shipping fee (other cities)
        assertEquals(70000, orderService.calculateShippingFee(null));
    }
    
    @Test
    void testCalculateShippingFee_EmptyCity() {
        // Empty city should return default shipping fee (other cities)
        assertEquals(70000, orderService.calculateShippingFee(""));
        assertEquals(70000, orderService.calculateShippingFee("   "));
    }
    
    @Test
    void testCalculateShippingFee_HanoiWithWhitespace() {
        // Hanoi with leading/trailing whitespace
        assertEquals(30000, orderService.calculateShippingFee("  Hanoi  "));
        assertEquals(30000, orderService.calculateShippingFee("  Hà Nội  "));
    }
}
