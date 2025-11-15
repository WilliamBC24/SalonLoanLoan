package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryInvoiceDetailDTO {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer orderedQuantity;
    private Integer unitPrice;
    private Integer subtotal;
}
