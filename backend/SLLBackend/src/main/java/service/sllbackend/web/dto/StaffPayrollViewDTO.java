package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffPayrollViewDTO {

    private Integer staffId;

    private String staffName;

    private String positionName;   // e.g., "Stylist", "Manager", etc.

    private Integer baseSalary; // fixed monthly salary

    private Integer commissionAmount; // total commission earned in the month

    private Integer bonusAmount; // optional bonus

    private Integer deductionsAmount; // social insurance, penalties, etc.

    private Integer totalPay; // base + commission + bonus - deductions
}
