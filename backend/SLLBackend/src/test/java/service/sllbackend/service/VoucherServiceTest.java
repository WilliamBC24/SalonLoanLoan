package service.sllbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.entity.Voucher;
import service.sllbackend.entity.VoucherStatus;
import service.sllbackend.enumerator.DiscountType;
import service.sllbackend.repository.VoucherRepo;
import service.sllbackend.repository.VoucherStatusRepo;
import service.sllbackend.service.impl.VoucherServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoucherServiceTest {

    @Mock
    private VoucherRepo voucherRepo;

    @Mock
    private VoucherStatusRepo voucherStatusRepo;

    @InjectMocks
    private VoucherServiceImpl voucherService;

    private VoucherStatus activeStatus;
    private Voucher voucher1;
    private Voucher voucher2;

    @BeforeEach
    void setUp() {
        activeStatus = VoucherStatus.builder()
                .id(1)
                .name("ACTIVE")
                .build();

        voucher1 = Voucher.builder()
                .id(1)
                .voucherCode("SAVE10")
                .voucherName("Save 10%")
                .voucherDescription("Get 10% off")
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(10)
                .voucherStatus(activeStatus)
                .build();

        voucher2 = Voucher.builder()
                .id(2)
                .voucherCode("FLAT50")
                .voucherName("Flat 50k off")
                .voucherDescription("Get 50k off")
                .discountType(DiscountType.AMOUNT)
                .discountAmount(50000)
                .voucherStatus(activeStatus)
                .build();
    }

    @Test
    void testGetVouchers_WithNoFilters_ShouldReturnAllVouchers() {
        // Arrange
        List<Voucher> allVouchers = List.of(voucher1, voucher2);
        when(voucherRepo.findAllWithStatus()).thenReturn(allVouchers);

        // Act
        List<Voucher> result = voucherService.getVouchers(null, null, null, null);

        // Assert
        assertEquals(2, result.size());
        verify(voucherRepo).findAllWithStatus();
    }

    @Test
    void testGetVouchers_WithCodeFilter_ShouldReturnFilteredVouchers() {
        // Arrange
        List<Voucher> allVouchers = List.of(voucher1, voucher2);
        when(voucherRepo.findAllWithStatus()).thenReturn(allVouchers);

        // Act
        List<Voucher> result = voucherService.getVouchers("SAVE", null, null, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals("SAVE10", result.get(0).getVoucherCode());
    }

    @Test
    void testGetVouchers_WithNameFilter_ShouldReturnFilteredVouchers() {
        // Arrange
        List<Voucher> allVouchers = List.of(voucher1, voucher2);
        when(voucherRepo.findAllWithStatus()).thenReturn(allVouchers);

        // Act
        List<Voucher> result = voucherService.getVouchers(null, "Flat", null, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Flat 50k off", result.get(0).getVoucherName());
    }

    @Test
    void testGetVouchers_WithDiscountTypeFilter_ShouldReturnFilteredVouchers() {
        // Arrange
        List<Voucher> allVouchers = List.of(voucher1, voucher2);
        when(voucherRepo.findAllWithStatus()).thenReturn(allVouchers);

        // Act
        List<Voucher> result = voucherService.getVouchers(null, null, "PERCENTAGE", null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(DiscountType.PERCENTAGE, result.get(0).getDiscountType());
    }

    @Test
    void testGetVouchers_WithStatusFilter_ShouldReturnFilteredVouchers() {
        // Arrange
        List<Voucher> allVouchers = List.of(voucher1, voucher2);
        when(voucherRepo.findAllWithStatus()).thenReturn(allVouchers);

        // Act
        List<Voucher> result = voucherService.getVouchers(null, null, null, 1);

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void testGetVouchers_WithMultipleFilters_ShouldReturnFilteredVouchers() {
        // Arrange
        List<Voucher> allVouchers = List.of(voucher1, voucher2);
        when(voucherRepo.findAllWithStatus()).thenReturn(allVouchers);

        // Act
        List<Voucher> result = voucherService.getVouchers("SAVE", "Save", "PERCENTAGE", 1);

        // Assert
        assertEquals(1, result.size());
        assertEquals("SAVE10", result.get(0).getVoucherCode());
    }

    @Test
    void testGetVoucherById_WithValidId_ShouldReturnVoucher() {
        // Arrange
        when(voucherRepo.findByIdWithStatus(1)).thenReturn(Optional.of(voucher1));

        // Act
        Voucher result = voucherService.getVoucherById(1);

        // Assert
        assertNotNull(result);
        assertEquals("SAVE10", result.getVoucherCode());
        verify(voucherRepo).findByIdWithStatus(1);
    }

    @Test
    void testGetVoucherById_WithInvalidId_ShouldReturnNull() {
        // Arrange
        when(voucherRepo.findByIdWithStatus(999)).thenReturn(Optional.empty());

        // Act
        Voucher result = voucherService.getVoucherById(999);

        // Assert
        assertNull(result);
    }

    @Test
    void testCreateVoucher_ShouldSaveAndReturnVoucher() {
        // Arrange
        when(voucherRepo.save(any(Voucher.class))).thenReturn(voucher1);

        // Act
        Voucher result = voucherService.createVoucher(voucher1);

        // Assert
        assertNotNull(result);
        assertEquals("SAVE10", result.getVoucherCode());
        verify(voucherRepo).save(voucher1);
    }

    @Test
    void testUpdateVoucher_WithValidId_ShouldUpdateAndReturnVoucher() {
        // Arrange
        Voucher existingVoucher = Voucher.builder()
                .id(1)
                .voucherCode("OLD_CODE")
                .voucherName("Old Name")
                .build();
        
        Voucher updatedVoucher = Voucher.builder()
                .voucherCode("NEW_CODE")
                .voucherName("New Name")
                .voucherDescription("New Description")
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(15)
                .effectiveFrom(LocalDateTime.now())
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .maxUsage(100)
                .voucherStatus(activeStatus)
                .build();

        when(voucherRepo.findById(1)).thenReturn(Optional.of(existingVoucher));
        when(voucherRepo.save(any(Voucher.class))).thenReturn(existingVoucher);

        // Act
        Voucher result = voucherService.updateVoucher(1, updatedVoucher);

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getVoucherName());
        verify(voucherRepo).findById(1);
        verify(voucherRepo).save(existingVoucher);
    }

    @Test
    void testUpdateVoucher_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(voucherRepo.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> voucherService.updateVoucher(999, voucher1)
        );

        assertTrue(exception.getMessage().contains("Voucher not found"));
    }

    @Test
    void testGetAllVoucherStatuses_ShouldReturnAllStatuses() {
        // Arrange
        List<VoucherStatus> statuses = List.of(activeStatus);
        when(voucherStatusRepo.findAll()).thenReturn(statuses);

        // Act
        List<VoucherStatus> result = voucherService.getAllVoucherStatuses();

        // Assert
        assertEquals(1, result.size());
        verify(voucherStatusRepo).findAll();
    }
}
