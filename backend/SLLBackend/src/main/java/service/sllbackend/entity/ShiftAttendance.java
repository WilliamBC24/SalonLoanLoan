package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.generator.EventType;
import service.sllbackend.enumerator.ShiftAttendanceStatus;


import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shift_attendance")
@Check(constraints = """
            (check_in IS NULL AND check_out IS NULL) OR
            (check_in IS NOT NULL AND check_out IS NULL) OR
            (check_in IS NOT NULL AND check_out > check_in)
        """)
public class ShiftAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_assignment_id", nullable = false)
    private ShiftAssignment shiftAssignment;

    @Column(name = "check_in")
    private LocalDateTime checkIn;

    @Column(name = "check_out")
    private LocalDateTime checkOut;

    //TODO: delete this in production
    @Generated(event = EventType.INSERT)
    @Column(
            name = "duration_minutes",
            insertable = false,
            updatable = false,
            columnDefinition = "INT GENERATED ALWAYS AS (" +
                    "CASE WHEN check_in IS NOT NULL AND check_out IS NOT NULL " +
                    "THEN ROUND(EXTRACT(EPOCH FROM (check_out - check_in)) / 60)::int " +
                    "ELSE NULL END" +
                    ") STORED"
    )
    private Integer durationMinutes;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "TEXT DEFAULT 'MISSED'")
    @Builder.Default
    private ShiftAttendanceStatus status = ShiftAttendanceStatus.MISSED;
}
