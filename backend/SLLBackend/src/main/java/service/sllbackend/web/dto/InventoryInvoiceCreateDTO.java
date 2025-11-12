package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryInvoiceCreateDTO {
    private Integer supplierId;
    private String note;
    private List<InventoryInvoiceItemDTO> items;
}
