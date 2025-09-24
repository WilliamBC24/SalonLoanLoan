package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "shift_assignment", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"shift_id", "assignment_date", "assigned_staff"})
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ShiftAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift;

    @Column(name = "assignment_date", nullable = false)
    private LocalDate assignmentDate;

    @ManyToOne
    @JoinColumn(name = "assigned_staff", nullable = false)
    private Staff assignedStaff;
}
