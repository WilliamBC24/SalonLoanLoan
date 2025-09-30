package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_change_history")
public class ProductChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false, columnDefinition = "TEXT")
    private String productName;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "product_description", nullable = false, columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "effective_from", nullable = false)
    @Builder.Default
    private LocalDateTime effectiveFrom = LocalDateTime.now();

    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;
}
