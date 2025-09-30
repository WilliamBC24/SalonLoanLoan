package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.PayrollAdjustment;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff_payroll_adjustment")
public class StaffPayrollAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type", nullable = false, columnDefinition = "payroll_adjustment_enum")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private PayrollAdjustment adjustmentType;

    @Column(nullable = false)
    private Integer amount;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "effective_date", nullable = false)
    @Builder.Default
    private LocalDate effectiveDate = LocalDate.now();
}
