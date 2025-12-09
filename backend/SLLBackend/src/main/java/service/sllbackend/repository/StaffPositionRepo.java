package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.StaffPosition;

public interface StaffPositionRepo extends JpaRepository<StaffPosition, Long> {
    StaffPosition findByPositionName(String name);
}
