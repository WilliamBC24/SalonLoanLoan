package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.Staff;
import service.sllbackend.enumerator.AccountStatus;

import java.util.List;

public interface StaffRepo extends JpaRepository<Staff, Integer> {
    Boolean existsByNameAndIdNot(String username, Long staffId);
}
