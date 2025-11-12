package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import service.sllbackend.entity.InventoryInvoice;
import service.sllbackend.enumerator.InventoryInvoiceStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface InventoryInvoiceRepo extends JpaRepository<InventoryInvoice, Integer>, JpaSpecificationExecutor<InventoryInvoice> {
    List<InventoryInvoice> findByInvoiceStatus(InventoryInvoiceStatus status);
    List<InventoryInvoice> findByStaffId(Integer staffId);
    List<InventoryInvoice> findBySupplierId(Integer supplierId);
    List<InventoryInvoice> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
