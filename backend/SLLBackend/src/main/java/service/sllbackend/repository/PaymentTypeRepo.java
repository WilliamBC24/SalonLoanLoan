package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.PaymentType;

public interface PaymentTypeRepo extends JpaRepository<PaymentType, Long> {
    PaymentType findByName(String name);
}
