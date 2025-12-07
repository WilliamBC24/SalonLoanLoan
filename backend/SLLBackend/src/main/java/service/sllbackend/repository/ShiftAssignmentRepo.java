package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.ShiftAssignment;
import service.sllbackend.entity.ShiftInstance;

import java.util.List;

public interface ShiftAssignmentRepo extends JpaRepository<ShiftAssignment, Long> {
    List<ShiftAssignment> findByShiftInstanceIn(List<ShiftInstance> instances);
}
