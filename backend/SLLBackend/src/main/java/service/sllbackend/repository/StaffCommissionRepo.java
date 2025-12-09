package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.StaffCommission;
import service.sllbackend.entity.StaffPosition;
import service.sllbackend.enumerator.CommissionType;

import java.util.Optional;

public interface StaffCommissionRepo extends JpaRepository<StaffCommission, Integer> {

    Optional<StaffCommission> findByPositionAndCommissionType(
            StaffPosition position,
            CommissionType commissionType
    );
}
