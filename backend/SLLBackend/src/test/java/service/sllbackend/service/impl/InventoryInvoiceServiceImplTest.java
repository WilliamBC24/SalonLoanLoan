package service.sllbackend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.entity.InventoryInvoice;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.Supplier;
import service.sllbackend.enumerator.InventoryInvoiceStatus;
import service.sllbackend.repository.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    private InventoryInvoice cancelledInvoice;
    private InventoryInvoice completedInvoice;
    private InventoryInvoice awaitingInvoice;

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

        cancelledInvoice = InventoryInvoice.builder()
                .id(1)
                .staff(testStaff)
                .supplier(testSupplier)
                .createdAt(LocalDateTime.now())
                .invoiceStatus(InventoryInvoiceStatus.CANCELLED)
                .note("Cancelled invoice")
                .build();

        completedInvoice = InventoryInvoice.builder()
                .id(2)
                .staff(testStaff)
                .supplier(testSupplier)
                .createdAt(LocalDateTime.now())
                .invoiceStatus(InventoryInvoiceStatus.COMPLETE)
                .note("Completed invoice")
                .build();

        awaitingInvoice = InventoryInvoice.builder()
                .id(3)
                .staff(testStaff)
                .supplier(testSupplier)
                .createdAt(LocalDateTime.now())
                .invoiceStatus(InventoryInvoiceStatus.AWAITING)
                .note("Awaiting invoice")
                .build();
    }

    @Test
    void updateInvoiceStatus_WhenInvoiceIsCancelled_ShouldThrowException() {
        // Arrange
        when(inventoryInvoiceRepo.findById(1)).thenReturn(Optional.of(cancelledInvoice));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryInvoiceService.updateInvoiceStatus(1, InventoryInvoiceStatus.AWAITING);
        });

        assertEquals("Cannot change status of a cancelled invoice", exception.getMessage());
        verify(inventoryInvoiceRepo, never()).save(any(InventoryInvoice.class));
    }

    @Test
    void updateInvoiceStatus_WhenInvoiceIsCancelled_ShouldNotAllowStatusChangeToComplete() {
        // Arrange
        when(inventoryInvoiceRepo.findById(1)).thenReturn(Optional.of(cancelledInvoice));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryInvoiceService.updateInvoiceStatus(1, InventoryInvoiceStatus.COMPLETE);
        });

        assertEquals("Cannot change status of a cancelled invoice", exception.getMessage());
        verify(inventoryInvoiceRepo, never()).save(any(InventoryInvoice.class));
    }

    @Test
    void updateInvoiceStatus_WhenInvoiceIsCompleted_ShouldThrowException() {
        // Arrange
        when(inventoryInvoiceRepo.findById(2)).thenReturn(Optional.of(completedInvoice));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryInvoiceService.updateInvoiceStatus(2, InventoryInvoiceStatus.AWAITING);
        });

        assertEquals("Cannot change status of a completed invoice", exception.getMessage());
        verify(inventoryInvoiceRepo, never()).save(any(InventoryInvoice.class));
    }

    @Test
    void updateInvoiceStatus_WhenInvoiceIsAwaiting_ShouldAllowStatusChange() {
        // Arrange
        when(inventoryInvoiceRepo.findById(3)).thenReturn(Optional.of(awaitingInvoice));
        when(inventoryInvoiceRepo.save(any(InventoryInvoice.class))).thenReturn(awaitingInvoice);

        // Act
        inventoryInvoiceService.updateInvoiceStatus(3, InventoryInvoiceStatus.CANCELLED);

        // Assert
        verify(inventoryInvoiceRepo).save(any(InventoryInvoice.class));
    }

    @Test
    void updateInvoiceStatus_WhenInvoiceNotFound_ShouldThrowException() {
        // Arrange
        when(inventoryInvoiceRepo.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            inventoryInvoiceService.updateInvoiceStatus(999, InventoryInvoiceStatus.COMPLETE);
        });

        assertEquals("Invoice not found with id: 999", exception.getMessage());
    }
}
