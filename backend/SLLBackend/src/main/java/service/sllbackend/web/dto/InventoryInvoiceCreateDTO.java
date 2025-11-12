package service.sllbackend.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    
    @NotNull(message = "Supplier ID is required")
    private Integer supplierId;
    
    private String note;
    
    @NotEmpty(message = "Invoice details cannot be empty")
    @Valid
    private List<InventoryInvoiceDetailDTO> details;
}
