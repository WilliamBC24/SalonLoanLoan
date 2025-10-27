package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import service.sllbackend.enumerator.DiscountType;


import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voucher")
@Check(constraints = """ 
        max_usage > 0 AND
        used_count <= max_usage AND
        (
                (discount_type = 'PERCENTAGE' AND discount_amount > 0 AND discount_amount <= 100)
        OR (discount_type = 'AMOUNT' AND discount_amount > 0)
    )
""")
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "voucher_name", nullable = false, columnDefinition = "TEXT")
    private String voucherName;

    @Column(name = "voucher_description", nullable = false, columnDefinition = "TEXT")
    private String voucherDescription;

    @Column(name = "voucher_code", nullable = false, unique = true, columnDefinition = "TEXT")
    private String voucherCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, columnDefinition = "TEXT DEFAULT 'AMOUNT'")
    @Builder.Default
    private DiscountType discountType = DiscountType.AMOUNT;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "effective_from", nullable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Builder.Default
    private LocalDateTime effectiveFrom = LocalDateTime.now();

    @Column(name = "effective_to", nullable = false)
    private LocalDateTime effectiveTo;

    @Column(name = "max_usage", nullable = false)
    private Integer maxUsage;

    @Column(name = "used_count", nullable = false, columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer usedCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_status", nullable = false)
    private VoucherStatus voucherStatus;
}
