package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.enums.DiscountTypeEnum;
import service.sllbackend.auth.entity.enums.PromotionStatusEnum;

import java.time.LocalDate;

@Entity
@Table(name = "promotion")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountTypeEnum discountType = DiscountTypeEnum.AMOUNT;

    @Column(name = "discount_amount", nullable = false)
    private Double discountAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "max_usage", nullable = false)
    private Integer maxUsage = 0;

    @Column(name = "used_count", nullable = false)
    private Integer usedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PromotionStatusEnum status = PromotionStatusEnum.DEACTIVATED;
}
