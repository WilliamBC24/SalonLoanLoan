package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.PromotionStatus;

public interface PromotionStatusRepo extends JpaRepository<PromotionStatus, Integer> {
}
