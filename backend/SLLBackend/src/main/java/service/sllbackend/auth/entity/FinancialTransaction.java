package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "financial_transaction")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FinancialTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category", nullable = false)
    private FinancialTransactionCategory category;

    @Column(nullable = false)
    private Double amount;

    private String note;

    @Column(nullable = false)
    private LocalDate date;
}
