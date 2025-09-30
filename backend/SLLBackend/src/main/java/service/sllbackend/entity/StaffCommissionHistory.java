package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.CommissionType;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff_commission_history")
public class StaffCommissionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_commission_id", nullable = false)
    private StaffCommission staffCommission;

    @Enumerated(EnumType.STRING)
    @Column(name = "commission_type", nullable = false, columnDefinition = "commission_type_enum")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private CommissionType commissionType;

    @Column(nullable = false)
    private Short commission;

    @Column(name = "effective_from", nullable = false)
    @Builder.Default
    private LocalDateTime effectiveFrom = LocalDateTime.now();

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;
}
