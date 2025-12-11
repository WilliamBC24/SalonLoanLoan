package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffPayrollAdjustment;
import service.sllbackend.enumerator.PayrollAdjustment;
import service.sllbackend.repository.StaffPayrollAdjustmentRepo;
import service.sllbackend.repository.StaffRepo;
import service.sllbackend.service.PayrollService;
import service.sllbackend.web.dto.StaffPayrollViewDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/manager/payroll")
@RequiredArgsConstructor
public class ManagerPayrollController {

    private final PayrollService payrollService;
    private final StaffRepo staffRepo;
    private final StaffPayrollAdjustmentRepo staffPayrollAdjustmentRepo;

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
            @RequestParam(value = "searchName", required = false) String searchName,
            Model model
    ) {
        YearMonth targetMonth = YearMonth.now().plusMonths(monthOffset);

        List<StaffPayrollViewDTO> payrollRows =
                payrollService.buildPayrollForMonth(targetMonth);

        if (searchName != null && !searchName.trim().isEmpty()) {
            String keyword = searchName.trim().toLowerCase();

            payrollRows = payrollRows.stream()
                    .filter(row -> row.getStaffName() != null
                            && row.getStaffName().toLowerCase().contains(keyword))
                    .toList();
        }

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
    @GetMapping("/staff/{staffId}")
    public String viewStaffPayrollForMonth(
            @PathVariable Integer staffId,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model
    ) {
        // Resolve target month/year (default = current month)
        YearMonth now = YearMonth.now();
        int resolvedMonth = (month != null) ? month : now.getMonthValue();
        int resolvedYear  = (year  != null) ? year  : now.getYear();
        YearMonth targetMonth = YearMonth.of(resolvedYear, resolvedMonth);

        // Build label like "March 2025"
        String monthLabel = targetMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                + " " + targetMonth.getYear();

        Staff staff = staffRepo.findById(staffId).get();

        // Get payroll summary for this staff + month
        // (You implement this in PayrollService)
        StaffPayrollViewDTO payrollRow =
                payrollService.buildPayrollForStaffMonth(staff, targetMonth);

        // Get adjustments for this staff in this month
        // You can implement via service OR directly via repo with a date range.
        LocalDate start = targetMonth.atDay(1);
        LocalDate end   = targetMonth.atEndOfMonth();

        List<StaffPayrollAdjustment> adjustments =
                staffPayrollAdjustmentRepo
                        .findByStaffAndEffectiveDateBetween(staff, start, end);

        // Staff display name
        String staffName = payrollRow != null
                ? payrollRow.getStaffName()
                : staffRepo.findById(staffId)
                .map(Staff::getName)
                .orElse("Unknown staff");

        model.addAttribute("staffId", staffId);
        model.addAttribute("staffName", staffName);

        model.addAttribute("payrollRow", payrollRow);
        model.addAttribute("adjustments", adjustments);

        model.addAttribute("selectedMonth", resolvedMonth);
        model.addAttribute("selectedYear", resolvedYear);
        model.addAttribute("currentMonthLabel", monthLabel);

        // For the month/year dropdowns in the HTML
        int currentYear = now.getYear();
        List<Integer> monthOptions = IntStream.rangeClosed(1, 12)
                .boxed()
                .toList();
        List<Integer> yearOptions = IntStream.rangeClosed(currentYear - 5, currentYear + 5)
                .boxed()
                .toList();

        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("yearOptions", yearOptions);

        return "manager-add-payroll-adjustment";
    }

    // ---------- 2) CREATE ADJUSTMENT (POST) ----------
    @PostMapping("/adjustment/create")
    public String createPayrollAdjustment(
            @RequestParam("staffId") Integer staffId,
            @RequestParam("adjustmentType") PayrollAdjustment adjustmentType,
            @RequestParam(value = "amount", required = false) Integer amount,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam("month") Integer month,
            @RequestParam("year") Integer year,
            RedirectAttributes redirectAttributes
    ) {
        // Redirect URL back to staff detail of same month/year
        String redirectUrl = String.format(
                "redirect:/manager/payroll/staff/%d?month=%d&year=%d",
                staffId, month, year
        );

        try {
            // Validate staff
            Staff staff = staffRepo.findById(staffId)
                    .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

            // amount is optional in the form, but DB has amount > 0 NOT NULL constraint
            if (amount == null || amount <= 0) {
                redirectAttributes.addFlashAttribute("error",
                        "Amount must be greater than 0.");
                return redirectUrl;
            }

            LocalDate effectiveDate;
            YearMonth current = YearMonth.now();
            YearMonth target = YearMonth.of(year, month);

            if (target.equals(current)) {
                effectiveDate = LocalDate.now();  // use actual current day
            } else {
                effectiveDate = target.atDay(1);  // use 01/MM/YYYY
            }

            StaffPayrollAdjustment adjustment = StaffPayrollAdjustment.builder()
                    .staff(staff)
                    .adjustmentType(adjustmentType)
                    .amount(amount)
                    .note((note != null && !note.isBlank()) ? note.trim() : null)
                    .effectiveDate(effectiveDate)
                    .build();

            staffPayrollAdjustmentRepo.save(adjustment);

            redirectAttributes.addFlashAttribute("success",
                    "Payroll adjustment created successfully.");
            return redirectUrl;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to create payroll adjustment: " + e.getMessage());
            return redirectUrl;
        }
    }
}
