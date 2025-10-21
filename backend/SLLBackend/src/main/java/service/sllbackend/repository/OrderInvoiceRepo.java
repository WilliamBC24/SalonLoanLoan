package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.CustomerInfo;
import service.sllbackend.entity.OrderInvoice;

import java.util.List;

@Repository
public interface OrderInvoiceRepo extends JpaRepository<OrderInvoice, Integer> {
    List<OrderInvoice> findByCustomerInfo_PhoneNumberOrderByCreatedAtDesc(String phoneNumber);
}
