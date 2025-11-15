package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.InventoryInvoiceStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryInvoiceViewDTO {
    private Integer id;
    private Integer staffId;
    private String staffName;
    private Integer supplierId;
    private String supplierName;
    private LocalDateTime createdAt;
    private String note;
    private InventoryInvoiceStatus invoiceStatus;
    private List<InventoryInvoiceDetailDTO> details;
    private Integer totalAmount;
}
