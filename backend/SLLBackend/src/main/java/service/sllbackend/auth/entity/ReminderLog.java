package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.enums.ReminderReasonEnum;
import service.sllbackend.auth.entity.enums.ReminderTypeEnum;

import java.time.LocalDate;

@Entity
@Table(name = "reminder_log")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReminderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_reason", nullable = false)
    private ReminderReasonEnum reminderReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_type", nullable = false)
    private ReminderTypeEnum reminderType;

    @Column(name = "reminded_date")
    private LocalDate remindedDate;
}
