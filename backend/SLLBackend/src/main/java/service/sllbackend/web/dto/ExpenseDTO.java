package service.sllbackend.web.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseDTO {
    private Integer expenseCategoryId;
    private Integer amount;
    private LocalDate dateIncurred;
    private String note;
}

