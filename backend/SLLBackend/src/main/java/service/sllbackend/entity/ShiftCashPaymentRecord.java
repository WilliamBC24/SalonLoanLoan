package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shift_cash_payment_record")
public class ShiftCashPaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_instance_id", nullable = false)
    private ShiftInstance shiftInstance;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "recorded_at", nullable = false)
    @Builder.Default
    private LocalDateTime recordedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_staff_id", nullable = false)
    private Staff responsibleStaff;

    @Column(columnDefinition = "TEXT")
    private String note;
}
