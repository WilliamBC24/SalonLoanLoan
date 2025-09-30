package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.DiscountType;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "promotion")
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "promotion_name", nullable = false, columnDefinition = "TEXT")
    private String promotionName;

    @Column(name = "promotion_description", columnDefinition = "TEXT")
    private String promotionDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, columnDefinition = "discount_type_enum")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Builder.Default
    private DiscountType discountType = DiscountType.AMOUNT;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "effective_from", nullable = false)
    @Builder.Default
    private LocalDateTime effectiveFrom = LocalDateTime.now();

    @Column(name = "effective_to", nullable = false)
    private LocalDateTime effectiveTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_status", nullable = false)
    private PromotionStatus promotionStatus;
}
