package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reminder_log")
public class ReminderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", nullable = false)
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_reason_id", nullable = false)
    private ReminderReason reminderReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_type_id", nullable = false)
    private ReminderType reminderType;

    @Column(name = "reminded_date")
    private LocalDate remindedDate;
}
