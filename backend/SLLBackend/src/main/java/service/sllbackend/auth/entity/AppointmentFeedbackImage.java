package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appointment_feedback_image")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AppointmentFeedbackImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_feedback_id", nullable = false)
    private AppointmentFeedback appointmentFeedback;

    @Column(name = "image_path", nullable = false)
    private String imagePath;
}
