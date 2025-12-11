package service.sllbackend.web.mvc;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.sllbackend.entity.Expense;
import service.sllbackend.entity.ExpenseCategory;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.repository.ExpenseCategoryRepo;
import service.sllbackend.repository.ExpenseRepo;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.service.ReportService;
import service.sllbackend.web.dto.*;

import java.io.IOException;
import java.io.OutputStream;
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
    private final ExpenseCategoryRepo expenseCategoryRepo;
    private final ExpenseRepo expenseRepo;

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
        List<ExpenseSummaryDTO> expenseSummary = reportService.getExpenseSummary(targetMonth);

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
        model.addAttribute("expenseSummary", expenseSummary);

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

    @GetMapping("/expense/list")
    public String expenseReport(@RequestParam(value = "month", required = false) Integer month,
                                @RequestParam(value = "year", required = false) Integer year,
                                Model model) {
        YearMonth targetMonth = resolveMonthYear(month, year);
        addCommonFilters(model, targetMonth);

        List<ExpenseSummaryDTO> expenseSummary =
                reportService.getExpenseSummary(targetMonth);

        model.addAttribute("expenseSummary", expenseSummary);

        return "manager-expense-list";
    }

    @GetMapping("/expense")
    public String createExpense(Model model) {

        if (!model.containsAttribute("expenseDto")) {
            model.addAttribute("expenseDto", new ExpenseDTO());
        }

        List<ExpenseCategory> expenseCategories = expenseCategoryRepo.findAll();
        model.addAttribute("categories", expenseCategories);

        return "manager-create-expense";
    }

    @PostMapping("/expense/create")
    public String saveExpense(@ModelAttribute ExpenseDTO expenseDTO,
                              RedirectAttributes redirectAttributes) {
        try {

            ExpenseCategory category = expenseCategoryRepo.findById((long) expenseDTO.getExpenseCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category"));

            if (expenseDTO.getAmount() == null || expenseDTO.getAmount() <= 0) {
                redirectAttributes.addFlashAttribute("error", "Amount must be greater than 0");
                redirectAttributes.addFlashAttribute("expenseDto", expenseDTO);
                return "redirect:/manager/report/expense";
            }

            if (expenseDTO.getDateIncurred() == null) {
                redirectAttributes.addFlashAttribute("error", "Date incurred is required");
                redirectAttributes.addFlashAttribute("expenseDto", expenseDTO);
                return "redirect:/manager/report/expense";
            }

            // Build entity
            Expense expense = Expense.builder()
                    .expenseCategory(category)
                    .amount(expenseDTO.getAmount())
                    .dateIncurred(expenseDTO.getDateIncurred())
                    .note(expenseDTO.getNote())
                    .build();

            expenseRepo.save(expense);

            redirectAttributes.addFlashAttribute("success", "Expense created successfully!");
            return "redirect:/manager/report/expense";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            redirectAttributes.addFlashAttribute("expenseDto", expenseDTO);
            return "redirect:/manager/report/expense";
        }
    }
    @GetMapping("/appointment/export")
    public void exportAppointmentReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "staffId", required = false) Integer staffId,
            HttpServletResponse response
    ) throws IOException {

        YearMonth targetMonth = resolveMonthYear(month, year);
        AppointmentReportDTO report = reportService.getAppointmentReport(targetMonth, staffId);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Appointment Report");
            int rowIdx = 0;

            // ==== SUMMARY (top) ====
            Row summary1 = sheet.createRow(rowIdx++);
            summary1.createCell(0).setCellValue("Month");
            summary1.createCell(1).setCellValue(targetMonth.toString());

            Row summary2 = sheet.createRow(rowIdx++);
            summary2.createCell(0).setCellValue("Total Appointments");
            summary2.createCell(1).setCellValue(report.getAppointmentCount());

            Row summary3 = sheet.createRow(rowIdx++);
            summary3.createCell(0).setCellValue("Total Revenue");
            summary3.createCell(1).setCellValue(report.getTotalRevenue());

            // Blank row
            rowIdx++;

            // ==== DETAILS HEADER ====
            Row header = sheet.createRow(rowIdx++);
            int c = 0;
            header.createCell(c++).setCellValue("Invoice ID");
            header.createCell(c++).setCellValue("Appointment Code");
            header.createCell(c++).setCellValue("Scheduled At");
            header.createCell(c++).setCellValue("Customer Name");
            header.createCell(c++).setCellValue("Staff Name");
            header.createCell(c++).setCellValue("Total Price");
            header.createCell(c++).setCellValue("Discount");
            header.createCell(c++).setCellValue("Net Price");

            // ==== DETAILS ROWS ====
            if (report.getRows() != null) {
                for (AppointmentReportRowDTO r : report.getRows()) {
                    Row row = sheet.createRow(rowIdx++);
                    int col = 0;
                    row.createCell(col++).setCellValue(
                            r.getInvoiceId() != null ? r.getInvoiceId() : 0
                    );
                    row.createCell(col++).setCellValue(
                            r.getAppointmentCode() != null ? r.getAppointmentCode() : ""
                    );
                    row.createCell(col++).setCellValue(
                            r.getScheduledAt() != null ? r.getScheduledAt().format(dtf) : ""
                    );
                    row.createCell(col++).setCellValue(
                            r.getCustomerName() != null ? r.getCustomerName() : ""
                    );
                    row.createCell(col++).setCellValue(
                            r.getStaffName() != null ? r.getStaffName() : ""
                    );
                    row.createCell(col++).setCellValue(r.getTotalPrice());
                    row.createCell(col++).setCellValue(r.getDiscount());
                    row.createCell(col++).setCellValue(r.getNetPrice());
                }
            }

            // Autosize
            for (int i = 0; i < header.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = "appointment-report-" + targetMonth + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }
        }
    }
    @GetMapping("/order/export")
    public void exportOrderReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            HttpServletResponse response
    ) throws IOException {

        YearMonth targetMonth = resolveMonthYear(month, year);
        OrderReportDTO report = reportService.getOrderReport(targetMonth);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Order Report");
            int rowIdx = 0;

            // ==== SUMMARY ====
            Row summary1 = sheet.createRow(rowIdx++);
            summary1.createCell(0).setCellValue("Month");
            summary1.createCell(1).setCellValue(targetMonth.toString());

            Row summary2 = sheet.createRow(rowIdx++);
            summary2.createCell(0).setCellValue("Total Orders");
            summary2.createCell(1).setCellValue(report.getOrderCount());

            Row summary3 = sheet.createRow(rowIdx++);
            summary3.createCell(0).setCellValue("Total Revenue");
            summary3.createCell(1).setCellValue(report.getTotalRevenue());

            rowIdx++;

            // ==== DETAILS HEADER ====
            Row header = sheet.createRow(rowIdx++);
            int c = 0;
            header.createCell(c++).setCellValue("Invoice ID");
            header.createCell(c++).setCellValue("Order Code");
            header.createCell(c++).setCellValue("Created At");
            header.createCell(c++).setCellValue("Customer Name");
            header.createCell(c++).setCellValue("Fulfillment Type");
            header.createCell(c++).setCellValue("Status");
            header.createCell(c++).setCellValue("Total Price");
            header.createCell(c++).setCellValue("Discount");
            header.createCell(c++).setCellValue("Net Price");

            // ==== DETAILS ROWS ====
            if (report.getRows() != null) {
                for (OrderReportRowDTO r : report.getRows()) {
                    Row row = sheet.createRow(rowIdx++);
                    int col = 0;
                    row.createCell(col++).setCellValue(
                            r.getInvoiceId() != null ? r.getInvoiceId() : 0
                    );
                    row.createCell(col++).setCellValue(
                            r.getOrderCode() != null ? r.getOrderCode() : ""
                    );
                    row.createCell(col++).setCellValue(
                            r.getCreatedAt() != null ? r.getCreatedAt().format(dtf) : ""
                    );
                    row.createCell(col++).setCellValue(
                            r.getCustomerName() != null ? r.getCustomerName() : ""
                    );
                    row.createCell(col++).setCellValue(
                            r.getFulfillmentType() != null ? r.getFulfillmentType() : ""
                    );
                    row.createCell(col++).setCellValue(
                            r.getStatusLabel() != null ? r.getStatusLabel() : ""
                    );
                    row.createCell(col++).setCellValue(r.getTotalPrice());
                    row.createCell(col++).setCellValue(r.getDiscount());
                    row.createCell(col++).setCellValue(r.getNetPrice());
                }
            }

            for (int i = 0; i < header.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = "order-report-" + targetMonth + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }
        }
    }
    @GetMapping("/supplier/export")
    public void exportSupplierReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            HttpServletResponse response
    ) throws IOException {

        YearMonth targetMonth = resolveMonthYear(month, year);
        List<SupplierSummaryDTO> supplierSummary = reportService.getSupplierSummary(targetMonth);

        // Sum total spent for summary
        int totalSpent = supplierSummary.stream()
                .mapToInt(SupplierSummaryDTO::getTotalSpent)
                .sum();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Supplier Report");
            int rowIdx = 0;

            // SUMMARY
            Row s1 = sheet.createRow(rowIdx++);
            s1.createCell(0).setCellValue("Month");
            s1.createCell(1).setCellValue(targetMonth.toString());

            Row s2 = sheet.createRow(rowIdx++);
            s2.createCell(0).setCellValue("Total Supply Cost");
            s2.createCell(1).setCellValue(totalSpent);

            rowIdx++;

            // HEADER
            Row header = sheet.createRow(rowIdx++);
            int c = 0;
            header.createCell(c++).setCellValue("Supplier Name");
            header.createCell(c++).setCellValue("Total Spent");
            header.createCell(c++).setCellValue("Percentage (%)");

            // ROWS
            for (SupplierSummaryDTO s : supplierSummary) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;
                row.createCell(col++).setCellValue(
                        s.getSupplierName() != null ? s.getSupplierName() : ""
                );
                row.createCell(col++).setCellValue(s.getTotalSpent());
                row.createCell(col++).setCellValue(
                        s.getPercentage() != null ? s.getPercentage() : 0.0
                );
            }

            for (int i = 0; i < header.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = "supplier-report-" + targetMonth + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }
        }
    }
    @GetMapping("/satisfaction/export")
    public void exportSatisfactionReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            HttpServletResponse response
    ) throws IOException {

        YearMonth targetMonth = resolveMonthYear(month, year);
        List<SatisfactionSummaryDTO> list =
                reportService.getSatisfactionSummary(targetMonth);

        long totalResponses = list.stream()
                .mapToLong(s -> s.getResponseCount() != null ? s.getResponseCount() : 0L)
                .sum();

        Double weightedAvg = null;
        if (totalResponses > 0) {
            double sum = 0.0;
            for (SatisfactionSummaryDTO s : list) {
                if (s.getAverageRating() != null && s.getResponseCount() != null) {
                    sum += s.getAverageRating() * s.getResponseCount();
                }
            }
            weightedAvg = sum / totalResponses;
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Satisfaction Report");
            int rowIdx = 0;

            // SUMMARY
            Row s1 = sheet.createRow(rowIdx++);
            s1.createCell(0).setCellValue("Month");
            s1.createCell(1).setCellValue(targetMonth.toString());

            Row s2 = sheet.createRow(rowIdx++);
            s2.createCell(0).setCellValue("Total Responses");
            s2.createCell(1).setCellValue(totalResponses);

            Row s3 = sheet.createRow(rowIdx++);
            s3.createCell(0).setCellValue("Weighted Average Rating");
            s3.createCell(1).setCellValue(weightedAvg != null ? weightedAvg : 0.0);

            rowIdx++;

            // HEADER
            Row header = sheet.createRow(rowIdx++);
            int c = 0;
            header.createCell(c++).setCellValue("Segment");
            header.createCell(c++).setCellValue("Average Rating");
            header.createCell(c++).setCellValue("Response Count");

            // ROWS
            for (SatisfactionSummaryDTO s : list) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;
                row.createCell(col++).setCellValue(
                        s.getSegmentLabel() != null ? s.getSegmentLabel() : ""
                );
                row.createCell(col++).setCellValue(
                        s.getAverageRating() != null ? s.getAverageRating() : 0.0
                );
                row.createCell(col++).setCellValue(
                        s.getResponseCount() != null ? s.getResponseCount() : 0L
                );
            }

            for (int i = 0; i < header.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = "satisfaction-report-" + targetMonth + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }
        }
    }
    @GetMapping("/sales/export")
    public void exportSalesReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            HttpServletResponse response
    ) throws IOException {

        YearMonth targetMonth = resolveMonthYear(month, year);
        List<SalesSummaryDTO> list =
                reportService.getSalesSummary(targetMonth);

        int totalSales = list.stream()
                .mapToInt(SalesSummaryDTO::getGrossSales)
                .sum();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sales Report");
            int rowIdx = 0;

            // SUMMARY
            Row s1 = sheet.createRow(rowIdx++);
            s1.createCell(0).setCellValue("Month");
            s1.createCell(1).setCellValue(targetMonth.toString());

            Row s2 = sheet.createRow(rowIdx++);
            s2.createCell(0).setCellValue("Total Gross Sales");
            s2.createCell(1).setCellValue(totalSales);

            rowIdx++;

            // HEADER
            Row header = sheet.createRow(rowIdx++);
            int c = 0;
            header.createCell(c++).setCellValue("Source");
            header.createCell(c++).setCellValue("Gross Sales");
            header.createCell(c++).setCellValue("Percentage (%)");

            // ROWS
            for (SalesSummaryDTO s : list) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;
                row.createCell(col++).setCellValue(
                        s.getSourceLabel() != null ? s.getSourceLabel() : ""
                );
                row.createCell(col++).setCellValue(s.getGrossSales());
                row.createCell(col++).setCellValue(
                        s.getPercentage() != null ? s.getPercentage() : 0.0
                );
            }

            for (int i = 0; i < header.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = "sales-report-" + targetMonth + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }
        }
    }
    @GetMapping("/expense/list/export")
    public void exportExpenseReport(
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            HttpServletResponse response
    ) throws IOException {

        YearMonth targetMonth = resolveMonthYear(month, year);
        List<ExpenseSummaryDTO> list =
                reportService.getExpenseSummary(targetMonth);

        long totalExpense = list.stream()
                .mapToLong(e -> e.getTotalAmount() != null ? e.getTotalAmount() : 0L)
                .sum();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Expense Report");
            int rowIdx = 0;

            // SUMMARY
            Row s1 = sheet.createRow(rowIdx++);
            s1.createCell(0).setCellValue("Month");
            s1.createCell(1).setCellValue(targetMonth.toString());

            Row s2 = sheet.createRow(rowIdx++);
            s2.createCell(0).setCellValue("Total Expense");
            s2.createCell(1).setCellValue(totalExpense);

            rowIdx++;

            // HEADER
            Row header = sheet.createRow(rowIdx++);
            int c = 0;
            header.createCell(c++).setCellValue("Category");
            header.createCell(c++).setCellValue("Total Amount");
            header.createCell(c++).setCellValue("Percentage (%)");

            // ROWS
            for (ExpenseSummaryDTO e : list) {
                Row row = sheet.createRow(rowIdx++);
                int col = 0;
                row.createCell(col++).setCellValue(
                        e.getCategoryName() != null ? e.getCategoryName() : ""
                );
                row.createCell(col++).setCellValue(
                        e.getTotalAmount() != null ? e.getTotalAmount() : 0L
                );
                row.createCell(col++).setCellValue(
                        e.getPercentage() != null ? e.getPercentage() : 0.0
                );
            }

            for (int i = 0; i < header.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = "expense-report-" + targetMonth + ".xlsx";
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            try (OutputStream out = response.getOutputStream()) {
                workbook.write(out);
            }
        }
    }



}
