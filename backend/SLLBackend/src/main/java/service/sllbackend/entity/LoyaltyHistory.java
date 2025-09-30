package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loyalty_history")
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

    @Column(name = "creditted_date", nullable = false)
    @Builder.Default
    private LocalDate credittedDate = LocalDate.now();
}
