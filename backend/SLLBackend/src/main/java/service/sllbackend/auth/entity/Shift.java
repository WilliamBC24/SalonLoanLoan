package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shift", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"shift_start", "shift_end"})
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shift_start", nullable = false)
    private LocalDateTime shiftStart;

    @Column(name = "shift_end", nullable = false)
    private LocalDateTime shiftEnd;
}
