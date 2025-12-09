package service.sllbackend.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesSummaryDTO {

    /**
     * e.g. "Appointments", "Product Orders"
     */
    private String sourceLabel;

    /**
     * Total gross sales of this source in the month.
     */
    private int grossSales;

    /**
     * Percentage of total sales.
     * Example: 60.0 = 60%
     */
    private Double percentage;
}

