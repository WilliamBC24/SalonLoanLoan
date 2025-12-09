package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.StaffPayroll;

import java.time.LocalDate;
import java.util.List;

public interface StaffPayrollRepo extends JpaRepository<StaffPayroll, Long> {
    List<StaffPayroll> findByPayPeriodStartLessThanEqualAndPayPeriodEndGreaterThanEqual(
            LocalDate endOfMonth,
            LocalDate startOfMonth
    );
}
