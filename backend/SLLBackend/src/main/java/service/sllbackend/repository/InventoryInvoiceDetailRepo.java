package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.InventoryInvoiceDetail;

import java.util.List;

public interface InventoryInvoiceDetailRepo extends JpaRepository<InventoryInvoiceDetail, Integer> {
    List<InventoryInvoiceDetail> findByInventoryInvoiceId(Integer invoiceId);
}
