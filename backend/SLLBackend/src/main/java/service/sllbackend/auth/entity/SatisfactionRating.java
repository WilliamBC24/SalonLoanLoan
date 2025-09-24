package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "satisfaction_rating")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SatisfactionRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(nullable = false)
    private Integer rating;

    private String comment;
}
