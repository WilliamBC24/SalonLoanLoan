package service.sllbackend.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryInvoiceDetailDTO {
    
    @NotNull(message = "Product ID is required")
    private Integer productId;
    
    @NotNull(message = "Ordered quantity is required")
    @Min(value = 1, message = "Ordered quantity must be at least 1")
    private Integer orderedQuantity;
    
    @NotNull(message = "Unit price is required")
    @Min(value = 1, message = "Unit price must be at least 1")
    private Integer unitPrice;
}
