package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryInvoiceDetailViewDTO {
    private Integer id;
    private String productName;
    private Integer productId;
    private Integer orderedQuantity;
    private Integer unitPrice;
    private Integer subtotal;
}
