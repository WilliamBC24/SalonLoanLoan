package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyOverviewDTO {

    private int appointmentRevenue;   // money from appointments
    private int orderRevenue;         // money from orders
    private int supplyCost;           // money spent on supplies
    private int netIncome;            // appointment + order - supplies

    /**
     * Net income growth vs last month (percentage).
     * Example: 12.5 = 12.5%
     * Can be null if last month has no data.
     */
    private Double netIncomeGrowthPct;
}
