package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.PaymentType;

import java.util.Optional;

@Repository
public interface PaymentTypeRepo extends JpaRepository<PaymentType, Integer> {
    Optional<PaymentType> findByName(String name);
}
