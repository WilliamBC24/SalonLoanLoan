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

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff_payroll")
@Check(constraints =
        "appointment_commission >= 0 " +
                "AND product_commission >= 0 " +
                "AND payroll_deduction >= 0 " +
                "AND payroll_bonus >= 0"
)

public class StaffPayroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(name = "pay_period_start", nullable = false)
    private LocalDate payPeriodStart;

    @Column(name = "pay_period_end", nullable = false)
    private LocalDate payPeriodEnd;

    @Column(name = "appointment_commission", nullable = false)
    private Integer appointmentCommission;

    @Column(name = "product_commission", nullable = false)
    private Integer productCommission;

    @Column(name = "payroll_deduction", nullable = false, columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer payrollDeduction = 0;

    @Column(name = "payroll_bonus", nullable = false, columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer payrollBonus = 0;

    //TODO: delete this in production
    @Generated(event = EventType.INSERT)
    @Column(
            name = "total_payment",
            insertable = false,
            updatable = false,
            columnDefinition =
                    "INT GENERATED ALWAYS AS " +
                            "(appointment_commission + product_commission + payroll_bonus - payroll_deduction) STORED"
    )
    private Integer totalPayment;

}
