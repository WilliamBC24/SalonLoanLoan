package service.sllbackend.service;

import service.sllbackend.web.dto.StaffPayrollViewDTO;

import java.time.YearMonth;
import java.util.List;

public interface PayrollService {
    List<StaffPayrollViewDTO> buildPayrollForMonth(YearMonth month);
}
