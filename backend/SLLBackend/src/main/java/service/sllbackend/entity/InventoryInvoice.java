package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.InventoryInvoiceStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory_invoice")
public class InventoryInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_status", nullable = false, columnDefinition = "TEXT DEFAULT 'AWAITING'")
    @Builder.Default
    private InventoryInvoiceStatus invoiceStatus = InventoryInvoiceStatus.AWAITING;
}
