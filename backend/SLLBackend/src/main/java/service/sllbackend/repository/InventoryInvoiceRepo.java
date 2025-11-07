package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.InventoryInvoice;

public interface InventoryInvoiceRepo extends JpaRepository<InventoryInvoice, Integer> {
}
