package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "financial_transaction_category")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FinancialTransactionCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
}
