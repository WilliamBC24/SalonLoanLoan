package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loyalty_history")
@Check(constraints = """
     (appointment_invoice_id IS NOT NULL AND order_id IS NULL) OR
            (appointment_invoice_id IS NULL AND order_id IS NOT NULL)
    """)
public class LoyaltyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loyalty_id", nullable = false)
    private Loyalty loyalty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_invoice_id")
    private AppointmentInvoice appointmentInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderInvoice order;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "creditted_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Builder.Default
    private LocalDateTime credittedDate = LocalDateTime.now();
}
