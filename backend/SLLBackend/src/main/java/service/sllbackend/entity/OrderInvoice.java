package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.FulfillmentType;
import service.sllbackend.enumerator.OrderStatus;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_invoice")
@Check(constraints = "total_price > 0")
public class OrderInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_info_id")
    private CustomerInfo customerInfo;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "payment_method", nullable = false, columnDefinition = "TEXT")
    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_type", nullable = false, columnDefinition = "TEXT")
    @Builder.Default
    private FulfillmentType fulfillmentType = FulfillmentType.DELIVERY;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, columnDefinition = "TEXT")
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
