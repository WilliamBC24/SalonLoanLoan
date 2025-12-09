package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.service.ReportService;
import service.sllbackend.web.dto.*;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Controller
@RequestMapping("/manager/report")
public class ManagerReportController {
    private final ReportService reportService;
    private final StaffAccountRepo staffAccountRepo;

    @GetMapping
    public String overallReport(
            @RequestParam(value = "month", required = false)
            @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            Model model
    ) {
        YearMonth targetMonth = (month != null) ? month : YearMonth.now();

        MonthlyOverviewDTO overview = reportService.getMonthlyOverview(targetMonth);
        List<SupplierSummaryDTO> supplierSummary = reportService.getSupplierSummary(targetMonth);
        List<SatisfactionSummaryDTO> satisfactionSummary = reportService.getSatisfactionSummary(targetMonth);
        List<SalesSummaryDTO> salesSummary = reportService.getSalesSummary(targetMonth);

        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        String currentMonthLabel = targetMonth.format(labelFormatter);

        model.addAttribute("currentMonthLabel", currentMonthLabel);

        model.addAttribute("monthlyAppointmentRevenue", overview.getAppointmentRevenue());
        model.addAttribute("monthlyOrderRevenue", overview.getOrderRevenue());
        model.addAttribute("monthlySupplyCost", overview.getSupplyCost());
        model.addAttribute("monthlyNetIncome", overview.getNetIncome());
        model.addAttribute("monthlyNetIncomeGrowth", overview.getNetIncomeGrowthPct());

        model.addAttribute("supplierSummary", supplierSummary);
        model.addAttribute("satisfactionSummary", satisfactionSummary);
        model.addAttribute("salesSummary", salesSummary);
        return "manager-report-overall";
    }

    private YearMonth resolveMonthYear(Integer month, Integer year) {
        YearMonth now = YearMonth.now();
        int resolvedMonth = (month != null) ? month : now.getMonthValue();
        int resolvedYear = (year != null) ? year : now.getYear();
        return YearMonth.of(resolvedYear, resolvedMonth);
    }

    private void addCommonFilters(Model model, YearMonth targetMonth) {
        int selectedMonth = targetMonth.getMonthValue();
        int selectedYear = targetMonth.getYear();

        // Month options 1–12
        List<Integer> monthOptions = IntStream.rangeClosed(1, 12)
                .boxed()
                .toList();

        // Year options: currentYear -10 .. currentYear +10
        int currentYear = YearMonth.now().getYear();
        int startYear = currentYear - 10;
        int endYear = currentYear + 10;

        List<Integer> yearOptions = IntStream.rangeClosed(startYear, endYear)
                .boxed()
                .toList();

        model.addAttribute("monthOptions", monthOptions);
        model.addAttribute("yearOptions", yearOptions);
        model.addAttribute("selectedMonth", selectedMonth);
        model.addAttribute("selectedYear", selectedYear);
    }

    // ========= 1) APPOINTMENT REPORT =========
    // URL: /manager/report/appointment
    // View: manager-appointment-report.html

    @GetMapping("/appointment")
    public String appointmentReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "staffId", required = false) Integer staffId,
            Model model
    ) {
        YearMonth targetMonth = resolveMonthYear(month, year);
        addCommonFilters(model, targetMonth);

        // Staff dropdown options
        List<StaffOptionDTO> staffOptions = staffAccountRepo.findAll().stream()
                .map(this::toStaffOptionDTO)
                .toList();

        AppointmentReportDTO report =
                reportService.getAppointmentReport(targetMonth, staffId);

        model.addAttribute("staffOptions", staffOptions);
        model.addAttribute("selectedStaffId", staffId);
        model.addAttribute("report", report);

        return "manager-appointment-report";
    }

    private StaffOptionDTO toStaffOptionDTO(StaffAccount staffAccount) {
        // adjust full name mapping to match your entity structure
        String fullName = (staffAccount.getStaff() != null)
                ? staffAccount.getStaff().getName()
                : staffAccount.getUsername();

        return StaffOptionDTO.builder()
                .id(staffAccount.getId())
                .displayName(fullName)
                .build();
    }

    // ========= 2) ORDER REPORT =========
    // URL: /manager/report/order
    // View: manager-order-report.html

    @GetMapping("/order")
    public String orderReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model
    ) {
        YearMonth targetMonth = resolveMonthYear(month, year);
        addCommonFilters(model, targetMonth);

        OrderReportDTO report = reportService.getOrderReport(targetMonth);

        model.addAttribute("report", report);

        return "manager-order-report";
    }

    // ========= 3) SUPPLIER REPORT =========
    // URL: /manager/report/supplier
    // View: manager-supplier-report.html
    // Same data structure as summary, but filtered by month/year.

    @GetMapping("/supplier")
    public String supplierReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model
    ) {
        YearMonth targetMonth = resolveMonthYear(month, year);
        addCommonFilters(model, targetMonth);

        List<SupplierSummaryDTO> supplierSummary =
                reportService.getSupplierSummary(targetMonth);

        model.addAttribute("supplierSummary", supplierSummary);

        return "manager-supplier-report";
    }

    // ========= 4) SATISFACTION REPORT =========
    // URL: /manager/report/satisfaction
    // View: manager-satisfaction-report.html

    @GetMapping("/satisfaction")
    public String satisfactionReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model
    ) {
        YearMonth targetMonth = resolveMonthYear(month, year);
        addCommonFilters(model, targetMonth);

        List<SatisfactionSummaryDTO> satisfactionSummary =
                reportService.getSatisfactionSummary(targetMonth);

        model.addAttribute("satisfactionSummary", satisfactionSummary);

        return "manager-satisfaction-report";
    }

    // ========= 5) SALES REPORT =========
    // URL: /manager/report/sales
    // View: manager-sales-report.html

    @GetMapping("/sales")
    public String salesReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            Model model
    ) {
        YearMonth targetMonth = resolveMonthYear(month, year);
        addCommonFilters(model, targetMonth);

        List<SalesSummaryDTO> salesSummary =
                reportService.getSalesSummary(targetMonth);

        model.addAttribute("salesSummary", salesSummary);

        return "manager-sales-report";
    }
}
