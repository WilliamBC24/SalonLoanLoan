package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffPayrollAdjustment;

import java.time.LocalDate;
import java.util.List;

public interface StaffPayrollAdjustmentRepo extends JpaRepository<StaffPayrollAdjustment, Integer> {

    List<StaffPayrollAdjustment> findByStaffAndEffectiveDateBetween(
            Staff staff,
            LocalDate start,
            LocalDate end
    );
}

