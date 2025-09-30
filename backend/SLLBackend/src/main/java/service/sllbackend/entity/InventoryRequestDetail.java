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
@Table(name = "inventory_request_detail")
public class InventoryRequestDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_request_id", nullable = false)
    private InventoryRequest inventoryRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_consignment_id", nullable = false)
    private InventoryConsignment inventoryConsignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "requested_quantity", nullable = false)
    private Integer requestedQuantity;

    @Column(name = "product_expiry_date", nullable = false)
    private LocalDate productExpiryDate;
}
