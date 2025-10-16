package service.sllbackend.service;

import java.util.List;

import service.sllbackend.entity.Promotion;
import service.sllbackend.entity.PromotionStatus;

public interface PromotionService {
    List<Promotion> getAllPromotions();
    
    Promotion getPromotionById(Integer id);
    
    List<PromotionStatus> getAllPromotionStatuses();
    
    Promotion createPromotion(Promotion promotion);
    
    Promotion updatePromotion(Integer id, Promotion promotion);
}
