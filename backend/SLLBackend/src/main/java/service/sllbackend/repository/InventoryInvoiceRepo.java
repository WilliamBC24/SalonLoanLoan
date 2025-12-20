package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.InventoryInvoice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryInvoiceRepo extends JpaRepository<InventoryInvoice, Integer> {
    
    @Query(value = "SELECT ii.* FROM inventory_invoice ii " +
           "JOIN staff s ON s.id = ii.staff_id " +
           "JOIN supplier s2 ON s2.id = ii.supplier_id " +
           "WHERE (CAST(:supplierId AS INTEGER) IS NULL OR ii.supplier_id = CAST(:supplierId AS INTEGER)) " +
           "AND (CAST(:status AS TEXT) IS NULL OR ii.invoice_status = CAST(:status AS TEXT)) " +
           "AND (CAST(:fromDate AS TIMESTAMP) IS NULL OR ii.created_at >= CAST(:fromDate AS TIMESTAMP)) " +
           "AND (CAST(:toDate AS TIMESTAMP) IS NULL OR ii.created_at <= CAST(:toDate AS TIMESTAMP)) " +
           "ORDER BY ii.created_at DESC", nativeQuery = true)
    List<InventoryInvoice> searchInvoices(
            @Param("supplierId") Integer supplierId,
            @Param("status") String status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
    
    @Query("SELECT ii FROM InventoryInvoice ii " +
           "JOIN FETCH ii.staff " +
           "JOIN FETCH ii.supplier " +
           "ORDER BY ii.createdAt DESC")
    List<InventoryInvoice> findAllWithDetails();
    
    @Query("SELECT ii FROM InventoryInvoice ii " +
           "JOIN FETCH ii.staff " +
           "JOIN FETCH ii.supplier " +
           "WHERE ii.id = :id")
    Optional<InventoryInvoice> findByIdWithDetails(@Param("id") Integer id);
}
