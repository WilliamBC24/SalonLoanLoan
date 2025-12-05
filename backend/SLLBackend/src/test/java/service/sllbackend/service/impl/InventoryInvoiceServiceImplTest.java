package service.sllbackend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.InventoryInvoiceStatus;
import service.sllbackend.repository.*;
import service.sllbackend.web.dto.InventoryInvoiceCreateDTO;
import service.sllbackend.web.dto.InventoryInvoiceItemDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryInvoiceServiceImplTest {

    @Mock
    private InventoryInvoiceRepo inventoryInvoiceRepo;
    
    @Mock
    private InventoryInvoiceDetailRepo inventoryInvoiceDetailRepo;
    
    @Mock
    private StaffRepo staffRepo;
    
    @Mock
    private SupplierRepo supplierRepo;
    
    @Mock
    private ProductRepo productRepo;
    
    @Mock
    private InventoryConsignmentRepo inventoryConsignmentRepo;
    
    @Mock
    private InventoryLotRepo inventoryLotRepo;
    
    @InjectMocks
    private InventoryInvoiceServiceImpl inventoryInvoiceService;
    
    private Staff testStaff;
    private Supplier testSupplier;
    private Product testProduct1;
    private Product testProduct2;
    
    @BeforeEach
    void setUp() {
        testStaff = Staff.builder()
                .id(1)
                .name("Test Staff")
                .build();
        
        testSupplier = Supplier.builder()
                .id(1)
                .supplierName("Test Supplier")
                .build();
        
        testProduct1 = Product.builder()
                .id(1)
                .productName("Product 1")
                .currentPrice(100)
                .build();
        
        testProduct2 = Product.builder()
                .id(2)
                .productName("Product 2")
                .currentPrice(200)
                .build();
    }
    
    @Test
    void createInvoiceRequest_shouldMergeDuplicateItemsWithSameProductAndPrice() {
        // Arrange
        when(staffRepo.findById(1)).thenReturn(Optional.of(testStaff));
        when(supplierRepo.findById(1)).thenReturn(Optional.of(testSupplier));
        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct1));
        
        InventoryInvoice savedInvoice = InventoryInvoice.builder()
                .id(1)
                .staff(testStaff)
                .supplier(testSupplier)
                .invoiceStatus(InventoryInvoiceStatus.AWAITING)
                .build();
        when(inventoryInvoiceRepo.save(any(InventoryInvoice.class))).thenReturn(savedInvoice);
        when(inventoryInvoiceDetailRepo.save(any(InventoryInvoiceDetail.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Create items with duplicates: same productId (1) and same unitPrice (50)
        List<InventoryInvoiceItemDTO> items = Arrays.asList(
                InventoryInvoiceItemDTO.builder()
                        .productId(1)
                        .orderedQuantity(10)
                        .unitPrice(50)
                        .build(),
                InventoryInvoiceItemDTO.builder()
                        .productId(1)
                        .orderedQuantity(20)
                        .unitPrice(50)
                        .build(),
                InventoryInvoiceItemDTO.builder()
                        .productId(1)
                        .orderedQuantity(5)
                        .unitPrice(50)
                        .build()
        );
        
        InventoryInvoiceCreateDTO dto = InventoryInvoiceCreateDTO.builder()
                .supplierId(1)
                .note("Test invoice")
                .items(items)
                .build();
        
        // Act
        inventoryInvoiceService.createInvoiceRequest(dto, 1);
        
        // Assert - should only save 1 detail (merged)
        ArgumentCaptor<InventoryInvoiceDetail> detailCaptor = ArgumentCaptor.forClass(InventoryInvoiceDetail.class);
        verify(inventoryInvoiceDetailRepo, times(1)).save(detailCaptor.capture());
        
        InventoryInvoiceDetail savedDetail = detailCaptor.getValue();
        assertEquals(1, savedDetail.getProduct().getId());
        assertEquals(35, savedDetail.getOrderedQuantity()); // 10 + 20 + 5 = 35
        assertEquals(50, savedDetail.getUnitPrice());
    }
    
    @Test
    void createInvoiceRequest_shouldNotMergeItemsWithDifferentPrices() {
        // Arrange
        when(staffRepo.findById(1)).thenReturn(Optional.of(testStaff));
        when(supplierRepo.findById(1)).thenReturn(Optional.of(testSupplier));
        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct1));
        
        InventoryInvoice savedInvoice = InventoryInvoice.builder()
                .id(1)
                .staff(testStaff)
                .supplier(testSupplier)
                .invoiceStatus(InventoryInvoiceStatus.AWAITING)
                .build();
        when(inventoryInvoiceRepo.save(any(InventoryInvoice.class))).thenReturn(savedInvoice);
        when(inventoryInvoiceDetailRepo.save(any(InventoryInvoiceDetail.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Create items with same productId but different unitPrices
        List<InventoryInvoiceItemDTO> items = Arrays.asList(
                InventoryInvoiceItemDTO.builder()
                        .productId(1)
                        .orderedQuantity(10)
                        .unitPrice(50)
                        .build(),
                InventoryInvoiceItemDTO.builder()
                        .productId(1)
                        .orderedQuantity(20)
                        .unitPrice(60) // Different price
                        .build()
        );
        
        InventoryInvoiceCreateDTO dto = InventoryInvoiceCreateDTO.builder()
                .supplierId(1)
                .note("Test invoice")
                .items(items)
                .build();
        
        // Act
        inventoryInvoiceService.createInvoiceRequest(dto, 1);
        
        // Assert - should save 2 details (not merged due to different prices)
        verify(inventoryInvoiceDetailRepo, times(2)).save(any(InventoryInvoiceDetail.class));
    }
    
    @Test
    void createInvoiceRequest_shouldNotMergeItemsWithDifferentProducts() {
        // Arrange
        when(staffRepo.findById(1)).thenReturn(Optional.of(testStaff));
        when(supplierRepo.findById(1)).thenReturn(Optional.of(testSupplier));
        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct1));
        when(productRepo.findById(2)).thenReturn(Optional.of(testProduct2));
        
        InventoryInvoice savedInvoice = InventoryInvoice.builder()
                .id(1)
                .staff(testStaff)
                .supplier(testSupplier)
                .invoiceStatus(InventoryInvoiceStatus.AWAITING)
                .build();
        when(inventoryInvoiceRepo.save(any(InventoryInvoice.class))).thenReturn(savedInvoice);
        when(inventoryInvoiceDetailRepo.save(any(InventoryInvoiceDetail.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Create items with different productIds but same unitPrice
        List<InventoryInvoiceItemDTO> items = Arrays.asList(
                InventoryInvoiceItemDTO.builder()
                        .productId(1)
                        .orderedQuantity(10)
                        .unitPrice(50)
                        .build(),
                InventoryInvoiceItemDTO.builder()
                        .productId(2) // Different product
                        .orderedQuantity(20)
                        .unitPrice(50)
                        .build()
        );
        
        InventoryInvoiceCreateDTO dto = InventoryInvoiceCreateDTO.builder()
                .supplierId(1)
                .note("Test invoice")
                .items(items)
                .build();
        
        // Act
        inventoryInvoiceService.createInvoiceRequest(dto, 1);
        
        // Assert - should save 2 details (not merged due to different products)
        verify(inventoryInvoiceDetailRepo, times(2)).save(any(InventoryInvoiceDetail.class));
    }
    
    @Test
    void createInvoiceRequest_shouldHandleComplexMergeScenario() {
        // Arrange
        when(staffRepo.findById(1)).thenReturn(Optional.of(testStaff));
        when(supplierRepo.findById(1)).thenReturn(Optional.of(testSupplier));
        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct1));
        when(productRepo.findById(2)).thenReturn(Optional.of(testProduct2));
        
        InventoryInvoice savedInvoice = InventoryInvoice.builder()
                .id(1)
                .staff(testStaff)
                .supplier(testSupplier)
                .invoiceStatus(InventoryInvoiceStatus.AWAITING)
                .build();
        when(inventoryInvoiceRepo.save(any(InventoryInvoice.class))).thenReturn(savedInvoice);
        when(inventoryInvoiceDetailRepo.save(any(InventoryInvoiceDetail.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        
        // Complex scenario:
        // Product 1 at price 50: 10 + 5 = 15 (merged)
        // Product 1 at price 60: 20 (separate)
        // Product 2 at price 50: 15 (separate)
        List<InventoryInvoiceItemDTO> items = Arrays.asList(
                InventoryInvoiceItemDTO.builder()
                        .productId(1)
                        .orderedQuantity(10)
                        .unitPrice(50)
                        .build(),
                InventoryInvoiceItemDTO.builder()
                        .productId(1)
                        .orderedQuantity(20)
                        .unitPrice(60)
                        .build(),
                InventoryInvoiceItemDTO.builder()
                        .productId(2)
                        .orderedQuantity(15)
                        .unitPrice(50)
                        .build(),
                InventoryInvoiceItemDTO.builder()
                        .productId(1)
                        .orderedQuantity(5)
                        .unitPrice(50) // Should be merged with first item
                        .build()
        );
        
        InventoryInvoiceCreateDTO dto = InventoryInvoiceCreateDTO.builder()
                .supplierId(1)
                .note("Test invoice")
                .items(items)
                .build();
        
        // Act
        inventoryInvoiceService.createInvoiceRequest(dto, 1);
        
        // Assert - should save 3 details
        // 1. Product 1 at price 50 with quantity 15 (merged)
        // 2. Product 1 at price 60 with quantity 20
        // 3. Product 2 at price 50 with quantity 15
        verify(inventoryInvoiceDetailRepo, times(3)).save(any(InventoryInvoiceDetail.class));
    }
    
    @Test
    void createInvoiceRequest_shouldHandleEmptyItemsList() {
        // Arrange
        when(staffRepo.findById(1)).thenReturn(Optional.of(testStaff));
        when(supplierRepo.findById(1)).thenReturn(Optional.of(testSupplier));
        
        InventoryInvoice savedInvoice = InventoryInvoice.builder()
                .id(1)
                .staff(testStaff)
                .supplier(testSupplier)
                .invoiceStatus(InventoryInvoiceStatus.AWAITING)
                .build();
        when(inventoryInvoiceRepo.save(any(InventoryInvoice.class))).thenReturn(savedInvoice);
        
        InventoryInvoiceCreateDTO dto = InventoryInvoiceCreateDTO.builder()
                .supplierId(1)
                .note("Test invoice")
                .items(Arrays.asList())
                .build();
        
        // Act
        InventoryInvoice result = inventoryInvoiceService.createInvoiceRequest(dto, 1);
        
        // Assert
        assertNotNull(result);
        verify(inventoryInvoiceDetailRepo, never()).save(any(InventoryInvoiceDetail.class));
    }
    
    @Test
    void createInvoiceRequest_shouldHandleNullItemsList() {
        // Arrange
        when(staffRepo.findById(1)).thenReturn(Optional.of(testStaff));
        when(supplierRepo.findById(1)).thenReturn(Optional.of(testSupplier));
        
        InventoryInvoice savedInvoice = InventoryInvoice.builder()
                .id(1)
                .staff(testStaff)
                .supplier(testSupplier)
                .invoiceStatus(InventoryInvoiceStatus.AWAITING)
                .build();
        when(inventoryInvoiceRepo.save(any(InventoryInvoice.class))).thenReturn(savedInvoice);
        
        InventoryInvoiceCreateDTO dto = InventoryInvoiceCreateDTO.builder()
                .supplierId(1)
                .note("Test invoice")
                .items(null)
                .build();
        
        // Act
        InventoryInvoice result = inventoryInvoiceService.createInvoiceRequest(dto, 1);
        
        // Assert
        assertNotNull(result);
        verify(inventoryInvoiceDetailRepo, never()).save(any(InventoryInvoiceDetail.class));
    }
}
