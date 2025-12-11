package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.CommissionType;
import service.sllbackend.enumerator.PayrollAdjustment;
import service.sllbackend.enumerator.StaffStatus;
import service.sllbackend.repository.*;
import service.sllbackend.service.ReportService;
import service.sllbackend.service.StaffService;
import service.sllbackend.web.dto.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final AppointmentInvoiceRepo appointmentInvoiceRepo;
    private final OrderInvoiceRepo orderInvoiceRepo;
    private final InventoryInvoiceDetailRepo inventoryInvoiceDetailRepo;
    private final SatisfactionRatingRepo satisfactionRatingRepo;
    private final ProductFeedbackRepo productFeedbackRepo;
    private final AppointmentDetailsRepo appointmentDetailsRepo;
    private final ExpenseRepo expenseRepo;
    private final StaffService staffService;
    private final StaffCurrentPositionRepo staffCurrentPositionRepo;
    private final StaffCommissionRepo staffCommissionRepo;
    private final RequestedServiceRepo requestedServiceRepo;
    private final StaffPayrollAdjustmentRepo staffPayrollAdjustmentRepo;
    // --- Helper record to keep monthly totals in one place ---
    // Added expenseTotal so net income subtracts it too
    private record MonthlyTotals(
            int appointmentRevenue,
            int orderRevenue,
            int supplyCost,
            int expenseTotal,
            int staffPayrollTotal
    ) {}

    private LocalDateTime startOf(YearMonth month) {
        return month.atDay(1).atStartOfDay();
    }

    private LocalDateTime startOfNext(YearMonth month) {
        return month.plusMonths(1).atDay(1).atStartOfDay();
    }

    /**
     * Calculate monthly totals (appointments, orders, supplies, other expenses).
     * Supply cost is aggregated from InventoryInvoiceDetail.
     * Expenses are aggregated from Expense.
     */
    private MonthlyTotals calculateMonthlyTotals(YearMonth month) {
        LocalDateTime start = startOf(month);
        LocalDateTime end = startOfNext(month);

        // ===== Appointment revenue =====
        int appointmentRevenue = appointmentInvoiceRepo
                .findByCreatedAtBetween(start, end)
                .stream()
                .mapToInt(AppointmentInvoice::getTotalPrice)
                .sum();

        // ===== Order revenue =====
        int orderRevenue = orderInvoiceRepo
                .findByCreatedAtBetween(start, end)
                .stream()
                .mapToInt(OrderInvoice::getTotalPrice)
                .sum();

        // ===== Supply cost =====
        List<InventoryInvoiceDetail> detailLines =
                inventoryInvoiceDetailRepo.findByInventoryInvoice_CreatedAtBetween(start, end);

        int supplyCost = detailLines.stream()
                .mapToInt(InventoryInvoiceDetail::getSubtotal)
                .sum();

        // ===== Other expenses (normal expenses) =====
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<Expense> expenses =
                expenseRepo.findByDateIncurredBetween(startDate, endDate);

        int expenseTotal = expenses.stream()
                .mapToInt(e -> (int) e.getAmount())
                .sum();

        // ===== Staff payroll cost (recalculated per staff) =====
        int staffPayrollTotal = 0;

        List<Staff> activeStaff = staffService.findAllByStatus(StaffStatus.ACTIVE);

        for (Staff staff : activeStaff) {
            staffPayrollTotal += calculateStaffTotalPayForPeriod(
                    staff,
                    start,      // LocalDateTime periodStart
                    end,        // LocalDateTime periodEnd
                    startDate,  // LocalDate dayStart
                    endDate     // LocalDate dayEnd
            );
        }


        return new MonthlyTotals(
                appointmentRevenue,
                orderRevenue,
                supplyCost,
                expenseTotal,
                staffPayrollTotal     // ← new field
        );
    }
    private StaffPosition getCurrentPositionOfStaff(Staff staff) {
        return staffCurrentPositionRepo.findByStaff_Id(staff.getId())
                .map(StaffCurrentPosition::getPosition)
                .orElse(null);
    }
    private Short getAppointmentCommissionPercentForStaff(Staff staff) {
        StaffPosition pos = getCurrentPositionOfStaff(staff);
        if (pos == null) {
            return 0;
        }

        Optional<StaffCommission> commissionOpt =
                staffCommissionRepo.findByPositionAndCommissionType(
                        pos,
                        CommissionType.APPOINTMENT
                );
        return commissionOpt
                .map(StaffCommission::getCommission)
                .orElse((short) 0);
    }
    private int calculateAppointmentCommissionForStaff(
            Staff staff,
            LocalDateTime periodStart,
            LocalDateTime periodEnd
    ) {
        // 1) Load commission rate for this staff's position
        Short commissionPercent = getAppointmentCommissionPercentForStaff(staff);

        if (commissionPercent == null || commissionPercent <= 0) {
            return 0;
        }

        // 2) Find all requested services where this staff is responsible,
        // and the appointment's scheduledAt is in [periodStart, periodEnd)
        List<RequestedService> requestedServices =
                requestedServiceRepo
                        .findByResponsibleStaffAndAppointment_ScheduledAtBetween(
                                staff,
                                periodStart,
                                periodEnd
                        );

        int totalCommission = 0;

        for (RequestedService rs : requestedServices) {
            Integer priceAtBooking = rs.getPriceAtBooking();
            if (priceAtBooking == null) {
                continue;
            }

            // price * percent / 100, all ints
            int commissionForService = priceAtBooking * commissionPercent / 100;
            totalCommission += commissionForService;
        }

        return totalCommission;
    }

    private int calculateStaffTotalPayForPeriod(
            Staff staff,
            LocalDateTime periodStart,
            LocalDateTime periodEnd,
            LocalDate dayStart,
            LocalDate dayEnd
    ) {
        // Commission from requested services in this period
        int appointmentCommission = calculateAppointmentCommissionForStaff(
                staff,
                periodStart,
                periodEnd
        );

        // Bonus & deductions in this period
        BonusDeduction bd = calculateBonusAndDeductionsForStaff(
                staff,
                dayStart,
                dayEnd
        );

        int baseSalary = 0; // plug in real base salary later if you have it

        return baseSalary
                + appointmentCommission
                + bd.bonus()
                - bd.deduction();
    }
    private BonusDeduction calculateBonusAndDeductionsForStaff(
            Staff staff,
            LocalDate periodStart,
            LocalDate periodEnd
    ) {
        List<StaffPayrollAdjustment> adjustments =
                staffPayrollAdjustmentRepo.findByStaffAndEffectiveDateBetween(
                        staff,
                        periodStart,
                        periodEnd
                );

        int bonus = 0;
        int deduction = 0;

        for (StaffPayrollAdjustment adj : adjustments) {
            int amount = adj.getAmount() != null ? adj.getAmount() : 0;
            if (adj.getAdjustmentType() == PayrollAdjustment.BONUS) {
                bonus += amount;
            } else if (adj.getAdjustmentType() == PayrollAdjustment.DEDUCTION) {
                deduction += amount;
            }
        }

        return new BonusDeduction(bonus, deduction);
    }
    private record BonusDeduction(int bonus, int deduction) {
    }


    @Override
    public MonthlyOverviewDTO getMonthlyOverview(YearMonth month) {
        YearMonth target = (month != null) ? month : YearMonth.now();

        MonthlyTotals current = calculateMonthlyTotals(target);
        int netIncome = current.appointmentRevenue()
                + current.orderRevenue()
                - current.supplyCost()
                - current.expenseTotal()
                - current.staffPayrollTotal();  // ← subtract salary/payroll cost

        YearMonth previousMonth = target.minusMonths(1);
        MonthlyTotals previous = calculateMonthlyTotals(previousMonth);
        int previousNetIncome = previous.appointmentRevenue()
                + previous.orderRevenue()
                - previous.supplyCost()
                - previous.expenseTotal()
                - previous.staffPayrollTotal();  // ← subtract salary/payroll cost
        ;  // subtract expenses as well

        Double growthPct = null;
        if (previousNetIncome != 0) {
            growthPct = ((double) (netIncome - previousNetIncome) / previousNetIncome) * 100.0;
        }

        return MonthlyOverviewDTO.builder()
                .appointmentRevenue(current.appointmentRevenue())
                .orderRevenue(current.orderRevenue())
                .supplyCost(current.supplyCost())
                .netIncome(netIncome)
                .netIncomeGrowthPct(growthPct)
                .build();
    }

    @Override
    public AppointmentReportDTO getAppointmentReport(YearMonth month, Integer staffId) {
        YearMonth target = (month != null) ? month : YearMonth.now();
        LocalDateTime start = startOf(target);
        LocalDateTime end   = startOfNext(target);

        // 1. Get all appointment invoices in the month
        List<AppointmentInvoice> invoices = appointmentInvoiceRepo
                .findByCreatedAtBetween(start, end);

        // 2. Optional filter by responsible staff (in-memory)
        List<AppointmentInvoice> filtered = invoices.stream()
                .filter(inv -> {
                    if (staffId == null) {
                        return true;
                    }
                    if (inv.getAppointment() == null) {
                        return false;
                    }

                    var appointment = inv.getAppointment();

                    if (appointment.getResponsibleStaffId() == null) {
                        return false;
                    }

                    return Objects.equals(
                            appointment.getResponsibleStaffId().getId(),
                            staffId
                    );
                })
                .toList();

        // 3. Map to row DTOs for the table
        List<AppointmentReportRowDTO> rows = filtered.stream()
                .map(inv -> {
                    var appointment = inv.getAppointment();
                    var appointmentDetails = appointmentDetailsRepo.findByAppointmentId((long) appointment.getId()).orElse(null);

                    String appointmentCode = (appointment != null) ? String.valueOf(appointment.getId()) : null;
                    LocalDateTime scheduledAt = (appointment != null) ? appointment.getScheduledAt() : null;

                    String customerName = null;
                    if (appointment != null && appointmentDetails.getUser() != null) {
                        customerName = appointmentDetails.getUser().getUsername();
                    }

                    String staffName = null;
                    if (appointment != null && appointment.getResponsibleStaffId() != null) {
                        staffName = appointment.getResponsibleStaffId().getName();
                    }

                    int totalPrice = inv.getTotalPrice();
                    int discount   = 0;
                    int netPrice   = totalPrice - discount;

                    return AppointmentReportRowDTO.builder()
                            .invoiceId(inv.getId())
                            .appointmentCode(appointmentCode)
                            .scheduledAt(scheduledAt)
                            .customerName(customerName)
                            .staffName(staffName)
                            .totalPrice(totalPrice)
                            .discount(discount)
                            .netPrice(netPrice)
                            .build();
                })
                .toList();

        int totalRevenue   = rows.stream().mapToInt(AppointmentReportRowDTO::getNetPrice).sum();
        int appointmentCount = rows.size();

        return AppointmentReportDTO.builder()
                .totalRevenue(totalRevenue)
                .appointmentCount(appointmentCount)
                .rows(rows)
                .build();
    }

    @Override
    public OrderReportDTO getOrderReport(YearMonth month) {
        YearMonth target = (month != null) ? month : YearMonth.now();
        LocalDateTime start = startOf(target);
        LocalDateTime end   = startOfNext(target);

        List<OrderInvoice> invoices = orderInvoiceRepo
                .findByCreatedAtBetween(start, end);

        List<OrderReportRowDTO> rows = invoices.stream()
                .map(inv -> {
                    String orderCode = String.valueOf(inv.getId());
                    LocalDateTime createdAt = inv.getCreatedAt();

                    String customerName = null;
                    if (inv.getUserAccount() != null) {
                        customerName = inv.getUserAccount().getUsername();
                    }

                    String fulfillmentType = (inv.getFulfillmentType() != null)
                            ? inv.getFulfillmentType().name()
                            : null;

                    String statusLabel = (inv.getOrderStatus() != null)
                            ? inv.getOrderStatus().name()
                            : null;

                    int totalPrice = inv.getTotalPrice();
                    int discount   = 0;
                    int netPrice   = totalPrice - discount;

                    return OrderReportRowDTO.builder()
                            .invoiceId(inv.getId())
                            .orderCode(orderCode)
                            .createdAt(createdAt)
                            .customerName(customerName)
                            .fulfillmentType(fulfillmentType)
                            .statusLabel(statusLabel)
                            .totalPrice(totalPrice)
                            .discount(discount)
                            .netPrice(netPrice)
                            .build();
                })
                .toList();

        int totalRevenue = rows.stream().mapToInt(OrderReportRowDTO::getNetPrice).sum();
        int orderCount   = rows.size();

        return OrderReportDTO.builder()
                .totalRevenue(totalRevenue)
                .orderCount(orderCount)
                .rows(rows)
                .build();
    }

    @Override
    public List<SupplierSummaryDTO> getSupplierSummary(YearMonth month) {
        YearMonth target = (month != null) ? month : YearMonth.now();
        LocalDateTime start = startOf(target);
        LocalDateTime end = startOfNext(target);

        List<InventoryInvoiceDetail> detailLines =
                inventoryInvoiceDetailRepo.findByInventoryInvoice_CreatedAtBetween(start, end);

        Map<Supplier, Integer> totalsBySupplier = new HashMap<>();

        for (InventoryInvoiceDetail detail : detailLines) {
            InventoryInvoice invoice = detail.getInventoryInvoice();
            if (invoice == null) continue;

            Supplier supplier = invoice.getSupplier();
            if (supplier == null) continue;

            int amount = detail.getSubtotal();
            totalsBySupplier.merge(supplier, amount, Integer::sum);
        }

        int grandTotal = totalsBySupplier.values().stream().mapToInt(Integer::intValue).sum();
        if (grandTotal == 0) {
            return Collections.emptyList();
        }

        return totalsBySupplier.entrySet().stream()
                .map(entry -> {
                    Supplier supplier = entry.getKey();
                    int total = entry.getValue();
                    double pct = (double) total * 100.0 / grandTotal;

                    return SupplierSummaryDTO.builder()
                            .supplierName(supplier.getSupplierName())
                            .totalSpent(total)
                            .percentage(pct)
                            .build();
                })
                .sorted(Comparator.comparingInt(SupplierSummaryDTO::getTotalSpent).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<SatisfactionSummaryDTO> getSatisfactionSummary(YearMonth month) {
        YearMonth target = (month != null) ? month : YearMonth.now();
        LocalDateTime start = startOf(target);
        LocalDateTime end   = startOfNext(target);

        List<SatisfactionRating> appointmentFeedbacks =
                satisfactionRatingRepo.findByAppointment_ScheduledAtBetween(start, end);

        List<Integer> appointmentRatings = appointmentFeedbacks.stream()
                .map(SatisfactionRating::getRating)
                .filter(Objects::nonNull)
                .map(Short::intValue)
                .toList();

        Double appointmentAvg = appointmentRatings.isEmpty()
                ? null
                : appointmentRatings.stream()
                .mapToInt(i -> i)
                .average()
                .orElse(0.0);

        List<ProductFeedback> productFeedbacks = productFeedbackRepo.findAll();

        List<Integer> productRatings = productFeedbacks.stream()
                .map(ProductFeedback::getRating)
                .filter(Objects::nonNull)
                .map(Short::intValue)
                .toList();

        Double productAvg = productRatings.isEmpty()
                ? null
                : productRatings.stream()
                .mapToInt(i -> i)
                .average()
                .orElse(0.0);

        List<SatisfactionSummaryDTO> result = new ArrayList<>();

        result.add(SatisfactionSummaryDTO.builder()
                .segmentLabel("Appointments")
                .averageRating(appointmentAvg)
                .responseCount((long) appointmentRatings.size())
                .build()
        );

        result.add(SatisfactionSummaryDTO.builder()
                .segmentLabel("Product Orders")
                .averageRating(productAvg)
                .responseCount((long) productRatings.size())
                .build()
        );

        return result;
    }

    @Override
    public List<SalesSummaryDTO> getSalesSummary(YearMonth month) {
        YearMonth target = (month != null) ? month : YearMonth.now();

        MonthlyTotals totals = calculateMonthlyTotals(target);
        int appointmentRevenue = totals.appointmentRevenue();
        int orderRevenue = totals.orderRevenue();

        int totalSales = appointmentRevenue + orderRevenue;
        if (totalSales == 0) {
            return Collections.emptyList();
        }

        double appointmentPct = (double) appointmentRevenue * 100.0 / totalSales;
        double orderPct = (double) orderRevenue * 100.0 / totalSales;

        List<SalesSummaryDTO> result = new ArrayList<>();

        result.add(SalesSummaryDTO.builder()
                .sourceLabel("Appointments")
                .grossSales(appointmentRevenue)
                .percentage(appointmentPct)
                .build()
        );

        result.add(SalesSummaryDTO.builder()
                .sourceLabel("Product Orders")
                .grossSales(orderRevenue)
                .percentage(orderPct)
                .build()
        );

        result.sort(Comparator.comparingInt(SalesSummaryDTO::getGrossSales).reversed());

        return result;
    }

    @Override
    public List<ExpenseSummaryDTO> getExpenseSummary(YearMonth month) {

        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        List<Expense> expenses = expenseRepo.findByDateIncurredBetween(start, end);

        if (expenses.isEmpty()) {
            return List.of();
        }

        long total = expenses.stream()
                .mapToLong(Expense::getAmount)
                .sum();

        Map<ExpenseCategory, Long> grouped = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getExpenseCategory,
                        Collectors.summingLong(Expense::getAmount)
                ));

        return grouped.entrySet().stream()
                .map(e -> ExpenseSummaryDTO.builder()
                        .categoryName(e.getKey().getName())
                        .totalAmount(e.getValue())
                        .percentage((e.getValue() * 100.0) / total)
                        .build()
                )
                .sorted(Comparator.comparing(ExpenseSummaryDTO::getTotalAmount).reversed())
                .toList();
    }
}
