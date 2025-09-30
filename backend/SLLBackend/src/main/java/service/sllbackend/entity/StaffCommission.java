package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.CommissionType;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff_commission", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"position_id", "commission_type"})
})
public class StaffCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private StaffPosition position;

    @Enumerated(EnumType.STRING)
    @Column(name = "commission_type", nullable = false, columnDefinition = "commission_type_enum")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private CommissionType commissionType;

    @Column(nullable = false)
    private Short commission;
}
