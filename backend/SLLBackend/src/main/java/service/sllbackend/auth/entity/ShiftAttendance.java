package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.enums.ShiftAttendanceStatusEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "shift_attendance")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ShiftAttendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private ShiftAssignment assignment;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    @Column(name = "duration_hours")
    private Double durationHours;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftAttendanceStatusEnum status = ShiftAttendanceStatusEnum.MISSED;
}
