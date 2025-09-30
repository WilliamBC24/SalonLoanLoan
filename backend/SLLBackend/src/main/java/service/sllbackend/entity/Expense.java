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
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_category", nullable = false)
    private ExpenseCategory expenseCategory;

    @Column(nullable = false)
    private Integer amount;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "date_incurred", nullable = false)
    private LocalDate dateIncurred;
}
