package service.sllbackend.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.entity.Promotion;
import service.sllbackend.entity.PromotionStatus;
import service.sllbackend.enumerator.DiscountType;
import service.sllbackend.repository.PromotionRepo;
import service.sllbackend.repository.PromotionStatusRepo;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceImplTest {

    @Mock
    private PromotionRepo promotionRepo;

    @Mock
    private PromotionStatusRepo promotionStatusRepo;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    private PromotionStatus activeStatus;

    @BeforeEach
    void setUp() {
        activeStatus = PromotionStatus.builder()
                .id(1)
                .name("ACTIVE")
                .build();
    }

    @Test
    void createPromotion_WithPercentageOver100_ShouldThrowException() {
        Promotion invalidPromotion = Promotion.builder()
                .promotionName("Invalid Promotion")
                .promotionDescription("This promotion has invalid discount")
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(150)
                .effectiveFrom(LocalDateTime.now())
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .promotionStatus(activeStatus)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> promotionService.createPromotion(invalidPromotion)
        );

        assertEquals("Discount percentage cannot exceed 100%", exception.getMessage());
        verify(promotionRepo, never()).save(any());
    }

    @Test
    void createPromotion_WithPercentageExactly100_ShouldSucceed() {
        Promotion validPromotion = Promotion.builder()
                .promotionName("Valid 100% Promotion")
                .promotionDescription("100% discount")
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(100)
                .effectiveFrom(LocalDateTime.now())
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .promotionStatus(activeStatus)
                .build();

        when(promotionRepo.save(any(Promotion.class))).thenReturn(validPromotion);

        Promotion result = promotionService.createPromotion(validPromotion);

        assertNotNull(result);
        verify(promotionRepo, times(1)).save(validPromotion);
    }

    @Test
    void createPromotion_WithPercentageBelow100_ShouldSucceed() {
        Promotion validPromotion = Promotion.builder()
                .promotionName("Valid Promotion")
                .promotionDescription("50% discount")
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(50)
                .effectiveFrom(LocalDateTime.now())
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .promotionStatus(activeStatus)
                .build();

        when(promotionRepo.save(any(Promotion.class))).thenReturn(validPromotion);

        Promotion result = promotionService.createPromotion(validPromotion);

        assertNotNull(result);
        verify(promotionRepo, times(1)).save(validPromotion);
    }

    @Test
    void createPromotion_WithAmountType_ShouldSucceedRegardlessOfValue() {
        Promotion validPromotion = Promotion.builder()
                .promotionName("Amount Promotion")
                .promotionDescription("High amount discount")
                .discountType(DiscountType.AMOUNT)
                .discountAmount(500000)
                .effectiveFrom(LocalDateTime.now())
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .promotionStatus(activeStatus)
                .build();

        when(promotionRepo.save(any(Promotion.class))).thenReturn(validPromotion);

        Promotion result = promotionService.createPromotion(validPromotion);

        assertNotNull(result);
        verify(promotionRepo, times(1)).save(validPromotion);
    }

    @Test
    void updatePromotion_WithPercentageOver100_ShouldThrowException() {
        Integer promotionId = 1;
        Promotion invalidPromotion = Promotion.builder()
                .promotionName("Invalid Update")
                .promotionDescription("Invalid discount update")
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(200)
                .effectiveFrom(LocalDateTime.now())
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .promotionStatus(activeStatus)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> promotionService.updatePromotion(promotionId, invalidPromotion)
        );

        assertEquals("Discount percentage cannot exceed 100%", exception.getMessage());
        verify(promotionRepo, never()).save(any());
    }

    @Test
    void updatePromotion_WithValidPercentage_ShouldSucceed() {
        Integer promotionId = 1;
        Promotion existingPromotion = Promotion.builder()
                .id(promotionId)
                .promotionName("Old Promotion")
                .promotionDescription("Old description")
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(10)
                .effectiveFrom(LocalDateTime.now())
                .effectiveTo(LocalDateTime.now().plusDays(30))
                .promotionStatus(activeStatus)
                .build();

        Promotion updatePromotion = Promotion.builder()
                .promotionName("Updated Promotion")
                .promotionDescription("Updated description")
                .discountType(DiscountType.PERCENTAGE)
                .discountAmount(50)
                .effectiveFrom(LocalDateTime.now())
                .effectiveTo(LocalDateTime.now().plusDays(60))
                .promotionStatus(activeStatus)
                .build();

        when(promotionRepo.findById(promotionId)).thenReturn(Optional.of(existingPromotion));
        when(promotionRepo.save(any(Promotion.class))).thenReturn(existingPromotion);

        Promotion result = promotionService.updatePromotion(promotionId, updatePromotion);

        assertNotNull(result);
        verify(promotionRepo, times(1)).findById(promotionId);
        verify(promotionRepo, times(1)).save(any(Promotion.class));
    }
}
