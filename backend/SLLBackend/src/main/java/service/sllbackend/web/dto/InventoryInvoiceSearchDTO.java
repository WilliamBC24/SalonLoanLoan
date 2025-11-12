package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.InventoryInvoiceStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryInvoiceSearchDTO {
    private Integer supplierId;
    private Integer staffId;
    private InventoryInvoiceStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
