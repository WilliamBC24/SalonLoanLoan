package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loyalty")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Loyalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccount user;

    @Column(nullable = false)
    private Integer point = 0;

    @ManyToOne
    @JoinColumn(name = "level_id", nullable = false)
    private LoyaltyLevel level;
}
