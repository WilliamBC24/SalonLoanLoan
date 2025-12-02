package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import service.sllbackend.enumerator.AppointmentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment")
@Check(constraints = "scheduled_at IS NULL OR scheduled_at >= registered_at")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "registered_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "TEXT DEFAULT 'PENDING'")
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_staff_id")
    private Staff preferredStaffId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_staff_id")
    private Staff responsibleStaffId;
}
