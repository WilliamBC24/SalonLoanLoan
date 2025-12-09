package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.ShiftAssignment;
import service.sllbackend.entity.ShiftInstance;
import service.sllbackend.entity.Staff;

import java.time.LocalDate;
import java.util.List;

public interface ShiftAssignmentRepo extends JpaRepository<ShiftAssignment, Long> {
    List<ShiftAssignment> findByShiftInstanceIn(List<ShiftInstance> instances);
    boolean existsByShiftInstanceAndAssignedStaff(ShiftInstance shift, Staff staff);
    void deleteByShiftInstanceAndAssignedStaff(ShiftInstance shift, Staff staff);
    List<ShiftAssignment> findByAssignedStaff_IdAndShiftInstance_ShiftDateBetween(
            Integer staffId,
            LocalDate start,
            LocalDate end
    );
}
