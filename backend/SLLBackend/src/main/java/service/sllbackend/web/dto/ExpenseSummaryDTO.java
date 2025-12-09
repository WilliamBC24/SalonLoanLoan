package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseSummaryDTO {

    private String categoryName;   // e.g. "Rent", "Supplies", "Utilities"
    private Long totalAmount;      // total amount spent in this category
    private Double percentage;     // % share of this category in total expenses

}

