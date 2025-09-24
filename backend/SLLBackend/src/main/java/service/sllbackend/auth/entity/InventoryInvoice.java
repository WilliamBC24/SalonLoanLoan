package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_invoice")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class InventoryInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "total_cost", nullable = false)
    private Double totalCost;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
