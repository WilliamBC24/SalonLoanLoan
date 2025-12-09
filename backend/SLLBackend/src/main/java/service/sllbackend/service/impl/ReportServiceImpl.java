package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.sllbackend.entity.AppointmentInvoice;
import service.sllbackend.entity.InventoryInvoice;
import service.sllbackend.entity.InventoryInvoiceDetail;
import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.entity.ProductFeedback;
import service.sllbackend.entity.SatisfactionRating;
import service.sllbackend.entity.Supplier;
import service.sllbackend.repository.*;
import service.sllbackend.service.ReportService;
import service.sllbackend.web.dto.*;

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

    // --- Helper record to keep monthly totals in one place ---
    private record MonthlyTotals(int appointmentRevenue, int orderRevenue, int supplyCost) {}

    private LocalDateTime startOf(YearMonth month) {
        return month.atDay(1).atStartOfDay();
    }

    private LocalDateTime startOfNext(YearMonth month) {
        return month.plusMonths(1).atDay(1).atStartOfDay();
    }

    /**
     * Calculate monthly totals (appointments, orders, supplies).
     * Supply cost is aggregated from InventoryInvoiceDetail.
     */
    private MonthlyTotals calculateMonthlyTotals(YearMonth month) {
        LocalDateTime start = startOf(month);
        LocalDateTime end = startOfNext(month);

        // Appointments revenue
        int appointmentRevenue = appointmentInvoiceRepo
                .findByCreatedAtBetween(start, end)                // adjust repo method if needed
                .stream()
                .mapToInt(AppointmentInvoice::getTotalPrice)       // adjust getter if needed
                .sum();

        // Orders revenue
        int orderRevenue = orderInvoiceRepo
                .findByCreatedAtBetween(start, end)                // adjust repo method if needed
                .stream()
                .mapToInt(OrderInvoice::getTotalPrice)             // adjust getter if needed
                .sum();

        // Supplies cost from InventoryInvoiceDetail
        // Preferred: query details directly by invoice date in repo:
        //   findByInventoryInvoiceCreatedAtBetween(start, end)
        List<InventoryInvoiceDetail> detailLines =
                inventoryInvoiceDetailRepo.findByInventoryInvoice_CreatedAtBetween(start, end);
        // If your repo is different, change the method name above.

        int supplyCost = detailLines.stream()
                .mapToInt(InventoryInvoiceDetail::getSubtotal)   // or quantity * unitPrice
                .sum();

        return new MonthlyTotals(appointmentRevenue, orderRevenue, supplyCost);
    }

    @Override
    public MonthlyOverviewDTO getMonthlyOverview(YearMonth month) {
        YearMonth target = (month != null) ? month : YearMonth.now();

        MonthlyTotals current = calculateMonthlyTotals(target);
        int netIncome = current.appointmentRevenue() + current.orderRevenue() - current.supplyCost();

        YearMonth previousMonth = target.minusMonths(1);
        MonthlyTotals previous = calculateMonthlyTotals(previousMonth);
        int previousNetIncome = previous.appointmentRevenue() + previous.orderRevenue() - previous.supplyCost();

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
                    // Adjust this part to match your Appointment/Staff mapping
                    if (inv.getAppointment() == null) {
                        return false;
                    }

                    var appointment = inv.getAppointment();

                    // Example assumption:
                    // Appointment has getResponsibleStaffAccount() -> StaffAccount -> getId()
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

                    // Safe null-ish mappings – tweak to match your real model
                    String appointmentCode = (appointment != null) ? String.valueOf(appointment.getId()) : null;
                    LocalDateTime scheduledAt = (appointment != null) ? appointment.getScheduledAt() : null;

                    String customerName = null;
                    if (appointment != null && appointmentDetails.getUser() != null) {
                        customerName = appointmentDetails.getUser().getUsername(); // adjust if needed
                    }

                    String staffName = null;
                    if (appointment != null && appointment.getResponsibleStaffId() != null) {
                            staffName = appointment.getResponsibleStaffId().getName();
                    }

                    int totalPrice = inv.getTotalPrice();  // already int
                    int discount   = 0;                    // if you have discount field, plug it in
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

        // 4. Summary fields
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

        // 1. Get all order invoices in the month
        List<OrderInvoice> invoices = orderInvoiceRepo
                .findByCreatedAtBetween(start, end);

        // (Optional) you might want to only include DELIVERED / PICKED_UP orders:
        // invoices = invoices.stream()
        //         .filter(inv -> inv.getOrderStatus() == OrderStatus.DELIVERED
        //                     || inv.getOrderStatus() == OrderStatus.PICKED_UP)
        //         .toList();

        // 2. Map to row DTOs for the table
        List<OrderReportRowDTO> rows = invoices.stream()
                .map(inv -> {
                    String orderCode = String.valueOf(inv.getId());  // adjust if different
                    LocalDateTime createdAt = inv.getCreatedAt();

                    String customerName = null;
                    if (inv.getUserAccount() != null) {
                        customerName = inv.getUserAccount().getUsername(); // adjust if needed
                    }

                    String fulfillmentType = (inv.getFulfillmentType() != null)
                            ? inv.getFulfillmentType().name()
                            : null;

                    String statusLabel = (inv.getOrderStatus() != null)
                            ? inv.getOrderStatus().name()
                            : null;

                    int totalPrice = inv.getTotalPrice();
                    int discount   = 0;                       // plug real discount field if you have it
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

        // 3. Summary fields
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

        // Get all detail lines for invoices in this month
        List<InventoryInvoiceDetail> detailLines =
                inventoryInvoiceDetailRepo.findByInventoryInvoice_CreatedAtBetween(start, end);
        // Again, adjust repo method name to match your real one.

        // Group by supplier via the header invoice
        Map<Supplier, Integer> totalsBySupplier = new HashMap<>();

        for (InventoryInvoiceDetail detail : detailLines) {
            InventoryInvoice invoice = detail.getInventoryInvoice();   // adjust getter if needed
            if (invoice == null) continue;

            Supplier supplier = invoice.getSupplier();                 // adjust getter if needed
            if (supplier == null) continue;

            int amount = detail.getSubtotal();                       // or quantity * unitPrice
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
                            .supplierName(supplier.getSupplierName())  // adjust getter if needed
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

        // === Appointments satisfaction (by appointment.scheduledAt) ===
        List<SatisfactionRating> appointmentFeedbacks =
                satisfactionRatingRepo.findByAppointment_ScheduledAtBetween(start, end);

        List<Integer> appointmentRatings = appointmentFeedbacks.stream()
                .map(r -> r.getRating())                // Short
                .filter(Objects::nonNull)
                .map(Short::intValue)                   // to Integer
                .toList();

        Double appointmentAvg = appointmentRatings.isEmpty()
                ? null
                : appointmentRatings.stream()
                .mapToInt(i -> i)
                .average()
                .orElse(0.0);

        List<ProductFeedback> productFeedbacks = productFeedbackRepo.findAll();

        List<Integer> productRatings = productFeedbacks.stream()
                .map(ProductFeedback::getRating)        // Short
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
}
