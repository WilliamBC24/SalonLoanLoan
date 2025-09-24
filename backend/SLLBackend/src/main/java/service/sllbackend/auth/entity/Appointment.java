package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.enums.AppointmentStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(nullable = false)
    private String name;

    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatusEnum status = AppointmentStatusEnum.PENDING;
}
