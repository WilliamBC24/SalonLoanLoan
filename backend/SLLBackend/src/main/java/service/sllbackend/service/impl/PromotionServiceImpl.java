package service.sllbackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.Promotion;
import service.sllbackend.entity.PromotionStatus;
import service.sllbackend.repository.PromotionRepo;
import service.sllbackend.repository.PromotionStatusRepo;
import service.sllbackend.service.PromotionService;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepo promotionRepo;
    private final PromotionStatusRepo promotionStatusRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Promotion> getAllPromotions() {
        return promotionRepo.findAllWithStatus();
    }

    @Override
    @Transactional(readOnly = true)
    public Promotion getPromotionById(Integer id) {
        return promotionRepo.findByIdWithStatus(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionStatus> getAllPromotionStatuses() {
        return promotionStatusRepo.findAll();
    }

    @Override
    @Transactional
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepo.save(promotion);
    }

    @Override
    @Transactional
    public Promotion updatePromotion(Integer id, Promotion promotion) {
        Promotion existingPromotion = promotionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + id));

        existingPromotion.setPromotionName(promotion.getPromotionName());
        existingPromotion.setPromotionDescription(promotion.getPromotionDescription());
        existingPromotion.setDiscountType(promotion.getDiscountType());
        existingPromotion.setDiscountAmount(promotion.getDiscountAmount());
        existingPromotion.setEffectiveFrom(promotion.getEffectiveFrom());
        existingPromotion.setEffectiveTo(promotion.getEffectiveTo());
        existingPromotion.setPromotionStatus(promotion.getPromotionStatus());

        return promotionRepo.save(existingPromotion);
    }
}
