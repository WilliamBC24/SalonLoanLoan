package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.InventoryRequestStatus;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory_request")
public class InventoryRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(name = "request_date", nullable = false)
    @Builder.Default
    private LocalDateTime requestDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_request_status", nullable = false, columnDefinition = "inventory_request_status_enum")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Builder.Default
    private InventoryRequestStatus inventoryRequestStatus = InventoryRequestStatus.PENDING;
}
