package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.Staff;

public interface StaffRepo extends JpaRepository<Staff, Integer> {
}
