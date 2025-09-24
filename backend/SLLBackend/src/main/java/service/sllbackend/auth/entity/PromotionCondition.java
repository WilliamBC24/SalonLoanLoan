package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "promotion_condition")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PromotionCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "min_user_level_id")
    private LoyaltyLevel minUserLevel;

    @Column(name = "min_bill")
    private Double minBill;

    @Column(name = "first_time_user")
    private Boolean firstTimeUser;
}
