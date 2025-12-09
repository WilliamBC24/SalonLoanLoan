package service.sllbackend.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierSummaryDTO {

    private String supplierName;
    private int totalSpent;

    /**
     * Percentage of total supply cost this month.
     * Example: 35.4 = 35.4%
     */
    private Double percentage;
}
