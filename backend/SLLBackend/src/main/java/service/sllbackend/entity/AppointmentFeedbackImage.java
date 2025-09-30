package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_feedback_image")
public class AppointmentFeedbackImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_feedback_id", nullable = false)
    private AppointmentFeedback appointmentFeedback;

    @Column(name = "image_path", nullable = false, columnDefinition = "TEXT")
    private String imagePath;
}
