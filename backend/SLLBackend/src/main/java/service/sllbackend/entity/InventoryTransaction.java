package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.InventoryTransactionType;
import service.sllbackend.enumerator.InventoryTransactionReason;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory_transaction")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_lot_id", nullable = false)
    private InventoryLot inventoryLot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, columnDefinition = "inventory_transaction_type_enum")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private InventoryTransactionType transactionType;

    @Column(name = "transaction_time", nullable = false)
    @Builder.Default
    private LocalDateTime transactionTime = LocalDateTime.now();

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "inventory_transaction_reason_enum")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private InventoryTransactionReason reason;
}
