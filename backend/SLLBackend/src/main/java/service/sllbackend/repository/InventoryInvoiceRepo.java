package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.InventoryInvoice;
import service.sllbackend.enumerator.InventoryInvoiceStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryInvoiceRepo extends JpaRepository<InventoryInvoice, Integer> {
    
    @Query("SELECT ii FROM InventoryInvoice ii " +
           "JOIN FETCH ii.staff " +
           "JOIN FETCH ii.supplier " +
           "WHERE (:supplierId IS NULL OR ii.supplier.id = :supplierId) " +
           "AND (:status IS NULL OR ii.invoiceStatus = :status) " +
           "AND (:fromDate IS NULL OR ii.createdAt >= :fromDate) " +
           "AND (:toDate IS NULL OR ii.createdAt <= :toDate) " +
           "ORDER BY ii.createdAt DESC")
    List<InventoryInvoice> searchInvoices(
            @Param("supplierId") Integer supplierId,
            @Param("status") InventoryInvoiceStatus status,
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
