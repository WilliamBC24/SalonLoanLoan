package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment_details", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"appointment_id", "user_id"})
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AppointmentDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(name = "scheduled_start", nullable = false)
    private LocalDateTime scheduledStart;

    @Column(name = "scheduled_end", nullable = false)
    private LocalDateTime scheduledEnd;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;
}
