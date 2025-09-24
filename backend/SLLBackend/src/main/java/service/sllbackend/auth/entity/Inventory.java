package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private InventoryInvoice invoice;

    @Column(nullable = false)
    private Integer quantity;
}
