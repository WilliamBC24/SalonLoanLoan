package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voucher_condition")
@Check(constraints = "min_bill IS NULL OR min_bill > 0")
public class VoucherCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "min_user_level_id")
    private LoyaltyLevel minUserLevel;

    @Column(name = "min_bill")
    private Integer minBill;

    @Column(name = "first_time_user")
    private Boolean firstTimeUser;
}
