package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.entity.OrderInvoiceDetails;

import java.util.List;

@Repository
public interface OrderInvoiceDetailsRepo extends JpaRepository<OrderInvoiceDetails, Integer> {
    List<OrderInvoiceDetails> findByOrderInvoice(OrderInvoice orderInvoice);
}
