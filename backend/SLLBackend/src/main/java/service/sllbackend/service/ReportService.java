package service.sllbackend.service;

import service.sllbackend.web.dto.*;

import java.time.YearMonth;
import java.util.List;

public interface ReportService {

    MonthlyOverviewDTO getMonthlyOverview(YearMonth month);

    // Detail endpoints:
    AppointmentReportDTO getAppointmentReport(YearMonth month, Integer staffId);

    OrderReportDTO getOrderReport(YearMonth month);

    List<SupplierSummaryDTO> getSupplierSummary(YearMonth month);

    List<SatisfactionSummaryDTO> getSatisfactionSummary(YearMonth month);

    List<SalesSummaryDTO> getSalesSummary(YearMonth month);

    List<ExpenseSummaryDTO> getExpenseSummary(YearMonth month);
}
