package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffCurrentPosition;

import java.util.List;
import java.util.Optional;

public interface StaffCurrentPositionRepo extends JpaRepository<StaffCurrentPosition, Long> {
    List<StaffCurrentPosition> findAllByStaff(Staff staff);
    Optional<StaffCurrentPosition> findByStaff_Id(Integer staffId);
}
