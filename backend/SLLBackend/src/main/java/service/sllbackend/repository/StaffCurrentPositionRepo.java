package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffCurrentPosition;

import java.util.List;

public interface StaffCurrentPositionRepo extends JpaRepository<StaffCurrentPosition, Long> {
    List<StaffCurrentPosition> findAllByStaff(Staff staff);
}
