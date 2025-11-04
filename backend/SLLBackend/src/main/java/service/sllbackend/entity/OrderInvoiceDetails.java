package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_invoice_details")
@Check(constraints = "quantity > 0 AND price_at_sale > 0")
public class OrderInvoiceDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_invoice_id", nullable = false)
    private OrderInvoice orderInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_sale", nullable = false)
    private Integer priceAtSale;

    //TODO: delete this in production
    @Generated(event = EventType.INSERT)
    @Column(
            name = "subtotal",
            insertable = false,
            updatable = false,
            columnDefinition =
                    "INT GENERATED ALWAYS AS " +
                            "(price_at_sale * quantity) STORED"
    )
    private Integer subtotal;
}
