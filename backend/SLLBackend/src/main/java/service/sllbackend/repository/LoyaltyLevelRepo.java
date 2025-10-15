package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.LoyaltyLevel;

public interface LoyaltyLevelRepo extends JpaRepository<LoyaltyLevel, Long> {
}
