package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.sllbackend.service.PayrollService;
import service.sllbackend.web.dto.StaffPayrollViewDTO;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/manager/payroll")
@RequiredArgsConstructor
public class ManagerPayrollController {

    private final PayrollService payrollService;

    /**
     * Show list of staff and their pay for the current (or offset) month.
     *
     * Example URL:
     *   /manager/payroll          -> current month
     *   /manager/payroll?monthOffset=-1 -> previous month
     *   /manager/payroll?monthOffset=1  -> next month
     */
    @GetMapping
    public String viewMonthlyPayroll(
            @RequestParam(value = "monthOffset", defaultValue = "0") int monthOffset,
            Model model
    ) {
        YearMonth targetMonth = YearMonth.now().plusMonths(monthOffset);

        List<StaffPayrollViewDTO> payrollRows =
                payrollService.buildPayrollForMonth(targetMonth);

        String monthLabel = targetMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                + " " + targetMonth.getYear();

        // --- totals ---
        int totalBaseSalary = payrollRows.stream()
                .mapToInt(StaffPayrollViewDTO::getBaseSalary)
                .sum();

        int totalCommission = payrollRows.stream()
                .mapToInt(StaffPayrollViewDTO::getCommissionAmount)
                .sum();

        int totalBonus = payrollRows.stream()
                .mapToInt(StaffPayrollViewDTO::getBonusAmount)
                .sum();

        int totalDeductions = payrollRows.stream()
                .mapToInt(StaffPayrollViewDTO::getDeductionsAmount)
                .sum();

        int totalTotalPay = payrollRows.stream()
                .mapToInt(StaffPayrollViewDTO::getTotalPay)
                .sum();

        model.addAttribute("payrollRows", payrollRows);
        model.addAttribute("currentMonthLabel", monthLabel);
        model.addAttribute("currentMonthYear", targetMonth);
        model.addAttribute("monthOffset", monthOffset);

        model.addAttribute("totalBaseSalary", totalBaseSalary);
        model.addAttribute("totalCommission", totalCommission);
        model.addAttribute("totalBonus", totalBonus);
        model.addAttribute("totalDeductions", totalDeductions);
        model.addAttribute("totalTotalPay", totalTotalPay);

        return "manager-payroll";
    }

}
