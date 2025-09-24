package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loyalty_level")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoyaltyLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "point_required", nullable = false)
    private Integer pointRequired;
}
