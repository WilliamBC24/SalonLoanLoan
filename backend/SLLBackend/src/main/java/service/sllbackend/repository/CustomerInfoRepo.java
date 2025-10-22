package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.CustomerInfo;

import java.util.Optional;

@Repository
public interface CustomerInfoRepo extends JpaRepository<CustomerInfo, Integer> {
    Optional<CustomerInfo> findByPhoneNumberAndShippingAddress(String phoneNumber, String shippingAddress);
}
