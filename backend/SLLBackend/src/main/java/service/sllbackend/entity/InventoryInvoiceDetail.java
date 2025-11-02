package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory_invoice_detail")
@Check(constraints = "ordered_quantity > 0 AND unit_price > 0")
public class InventoryInvoiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_invoice_id", nullable = false)
    private InventoryInvoice inventoryInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "ordered_quantity", nullable = false)
    private Integer orderedQuantity;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    //TODO: delete this in production
    @Generated(GenerationTime.ALWAYS)
    @Column(
            name = "subtotal",
            insertable = false,
            updatable = false,
            columnDefinition =
                    "INT GENERATED ALWAYS AS " +
                            "(unit_price * ordered_quantity) STORED"
    )
    private Integer subtotal;
}
