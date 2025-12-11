package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.CommissionType;
import service.sllbackend.enumerator.PayrollAdjustment;
import service.sllbackend.enumerator.StaffStatus;
import service.sllbackend.repository.StaffCurrentPositionRepo;
import service.sllbackend.service.PayrollService;
import service.sllbackend.service.StaffService;
import service.sllbackend.repository.RequestedServiceRepo;
import service.sllbackend.repository.StaffCommissionRepo;
import service.sllbackend.repository.StaffPayrollAdjustmentRepo;
import service.sllbackend.web.dto.StaffPayrollViewDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final StaffService staffService;
    private final RequestedServiceRepo requestedServiceRepo;
    private final StaffCommissionRepo staffCommissionRepo;
    private final StaffCurrentPositionRepo staffCurrentPositionRepo;
    private final StaffPayrollAdjustmentRepo staffPayrollAdjustmentRepo;

    @Override
    @Transactional(readOnly = true)
    public List<StaffPayrollViewDTO> buildPayrollForMonth(YearMonth targetMonth) {
        if (targetMonth == null) {
            targetMonth = YearMonth.now();
        }

        LocalDate periodStart = targetMonth.atDay(1);
        LocalDate periodEnd   = targetMonth.atEndOfMonth();

        LocalDateTime startDateTime = periodStart.atStartOfDay();
        // end is exclusive: first moment of the next day
        LocalDateTime endDateTime   = periodEnd.plusDays(1).atStartOfDay();

        // Get all active staff (or all staff, adjust to your needs)
        List<Staff> staffList = staffService.findAllByStatus(StaffStatus.ACTIVE);

        List<StaffPayrollViewDTO> result = new ArrayList<>();

        for (Staff staff : staffList) {
            // ===== 1. COMMISSION FROM REQUESTED SERVICES =====
            int appointmentCommission = calculateAppointmentCommissionForStaff(
                    staff,
                    startDateTime,
                    endDateTime
            );

            // If you later have product commission, calculate here
            int productCommission = 0;

            // ===== 2. BONUS & DEDUCTION FROM ADJUSTMENTS =====
            BonusDeduction bd = calculateBonusAndDeductionsForStaff(
                    staff,
                    periodStart,
                    periodEnd
            );

            int payrollBonus = bd.bonus;
            int payrollDeduction = bd.deduction;

            // ===== 3. TOTAL PAY (no base salary for now) =====
            int baseSalary = 0; // you can plug in fixed salary later if you add it
            int commissionTotal = appointmentCommission + productCommission;
            int totalPay = baseSalary + commissionTotal + payrollBonus - payrollDeduction;
            StaffPosition pos = getCurrentPositionOfStaff(staff);

            StaffPayrollViewDTO dto = StaffPayrollViewDTO.builder()
                    .staffId(staff.getId())
                    .staffName(staff.getName())
                    .positionName(pos != null ? pos.getPositionName() : null)
                    .baseSalary(baseSalary)
                    .commissionAmount(commissionTotal)
                    .bonusAmount(payrollBonus)
                    .deductionsAmount(payrollDeduction)
                    .totalPay(totalPay)
                    .build();

            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public StaffPayrollViewDTO buildPayrollForStaffMonth(Staff staff, YearMonth targetMonth) {
        if (staff == null) {
            throw new IllegalArgumentException("Staff must not be null");
        }

        if (targetMonth == null) {
            targetMonth = YearMonth.now();
        }

        LocalDate periodStart = targetMonth.atDay(1);
        LocalDate periodEnd   = targetMonth.atEndOfMonth();

        LocalDateTime startDateTime = periodStart.atStartOfDay();
        // end is exclusive: first moment of the next day
        LocalDateTime endDateTime   = periodEnd.plusDays(1).atStartOfDay();

        // ===== 1. COMMISSION FROM REQUESTED SERVICES =====
        int appointmentCommission = calculateAppointmentCommissionForStaff(
                staff,
                startDateTime,
                endDateTime
        );

        // If you later have product commission, calculate here
        int productCommission = 0;

        // ===== 2. BONUS & DEDUCTION FROM ADJUSTMENTS =====
        BonusDeduction bd = calculateBonusAndDeductionsForStaff(
                staff,
                periodStart,
                periodEnd
        );

        int payrollBonus      = bd.bonus;
        int payrollDeduction  = bd.deduction;

        // ===== 3. TOTAL PAY (no base salary for now, same as monthly method) =====
        int baseSalary       = 0; // plug in real salary if/when you have it
        int commissionTotal  = appointmentCommission + productCommission;
        int totalPay         = baseSalary + commissionTotal + payrollBonus - payrollDeduction;

        StaffPosition pos = getCurrentPositionOfStaff(staff);

        return StaffPayrollViewDTO.builder()
                .staffId(staff.getId())
                .staffName(staff.getName())
                .positionName(pos != null ? pos.getPositionName() : null)
                .baseSalary(baseSalary)
                .commissionAmount(commissionTotal)
                .bonusAmount(payrollBonus)
                .deductionsAmount(payrollDeduction)
                .totalPay(totalPay)
                .build();
    }


    /**
     * Calculate commission from all requested services for this staff
     * in the given date-time window, using price_at_booking * commission%.
     */
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

    /**
     * Look up commission% from staff_commission table for the staff's position.
     * Assumes commissionType = APPOINTMENT (adjust if you use a different enum value).
     */
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

    /**
     * Sum all BONUS and DEDUCTION adjustments for the staff in the month.
     */
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

    /**
     * Simple holder for bonus & deduction.
     */
    private record BonusDeduction(int bonus, int deduction) {
    }
    private StaffPosition getCurrentPositionOfStaff(Staff staff) {
        return staffCurrentPositionRepo.findByStaff_Id(staff.getId())
                .map(StaffCurrentPosition::getPosition)
                .orElse(null);
    }

}
