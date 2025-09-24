package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "expense")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category", nullable = false)
    private ExpenseCategory category;

    @Column(nullable = false)
    private Double amount;

    private String note;

    @Column(nullable = false)
    private LocalDate date;
}
