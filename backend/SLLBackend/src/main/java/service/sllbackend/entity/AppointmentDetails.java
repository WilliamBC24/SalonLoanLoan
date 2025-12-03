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

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_details")
@Check(constraints = """
        scheduled_end >= scheduled_start AND
        (
                (actual_start IS NULL AND actual_end IS NULL) OR
        (actual_start IS NULL AND actual_end IS NOT NULL) OR
        (actual_start IS NOT NULL AND actual_end IS NULL) OR
        (actual_start IS NOT NULL AND actual_end >= actual_start)
        )
""")

public class AppointmentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(name = "scheduled_start")
    private LocalDateTime scheduledStart;

    @Column(name = "scheduled_end")
    private LocalDateTime scheduledEnd;

    @Column(name = "actual_start")
    private LocalDateTime actualStart;

    @Column(name = "actual_end")
    private LocalDateTime actualEnd;

//    //TODO: delete this in production
//    @Generated(event = {EventType.INSERT, EventType.UPDATE})
//    @Column(
//            name = "duration_minutes",
//            insertable = false,
//            updatable = false,
//            columnDefinition = "INT GENERATED ALWAYS AS (" +
//                    "CASE WHEN actual_end IS NOT NULL AND actual_start IS NOT NULL " +
//                    "THEN ROUND(EXTRACT(EPOCH FROM (actual_end - actual_start)) / 60)::int" +
//                    "ELSE NULL END" +
//                    ") STORED"
//    )
//    private Integer durationMinutes;
    // Note: duration_minutes is a generated column in the database
}
