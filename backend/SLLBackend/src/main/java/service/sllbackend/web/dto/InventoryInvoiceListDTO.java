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
public class InventoryInvoiceListDTO {
    private Integer id;
    private String staffName;
    private String supplierName;
    private LocalDateTime createdAt;
    private InventoryInvoiceStatus invoiceStatus;
    private Integer totalItems;
    private Integer totalAmount;
}
