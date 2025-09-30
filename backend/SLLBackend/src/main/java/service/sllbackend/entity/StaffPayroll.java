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
@Table(name = "staff_payroll")
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

    @Column(name = "payroll_deduction", nullable = false)
    @Builder.Default
    private Integer payrollDeduction = 0;

    @Column(name = "payroll_bonus", nullable = false)
    @Builder.Default
    private Integer payrollBonus = 0;

    // Note: total_payment is a generated column in the database
}
