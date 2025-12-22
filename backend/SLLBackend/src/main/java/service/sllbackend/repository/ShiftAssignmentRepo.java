package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("""
        SELECT sa.assignedStaff.id
        FROM ShiftAssignment sa
        WHERE sa.shiftInstance.id = :shiftInstanceId
    """)
    List<Integer> findAssignedStaffIds(@Param("shiftInstanceId") Integer shiftInstanceId);
}
