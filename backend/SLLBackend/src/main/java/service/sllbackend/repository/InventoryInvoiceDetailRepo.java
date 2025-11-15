package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.InventoryInvoiceDetail;

import java.util.List;

public interface InventoryInvoiceDetailRepo extends JpaRepository<InventoryInvoiceDetail, Integer> {
    
    @Query("SELECT iid FROM InventoryInvoiceDetail iid " +
           "JOIN FETCH iid.product " +
           "WHERE iid.inventoryInvoice.id = :invoiceId")
    List<InventoryInvoiceDetail> findByInvoiceIdWithProduct(@Param("invoiceId") Integer invoiceId);
}
