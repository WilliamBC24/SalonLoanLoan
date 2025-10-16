package service.sllbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import service.sllbackend.entity.Promotion;

public interface PromotionRepo extends JpaRepository<Promotion, Integer> {

    @Query("select p from Promotion p left join fetch p.promotionStatus")
    List<Promotion> findAllWithStatus();

    @Query("select p from Promotion p left join fetch p.promotionStatus where p.id = :id")
    Optional<Promotion> findByIdWithStatus(@Param("id") Integer id);
}
