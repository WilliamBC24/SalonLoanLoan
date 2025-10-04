package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.StaffAccount;

import java.util.Optional;

public interface StaffAccountRepo extends JpaRepository<StaffAccount, Long> {
    Optional<StaffAccount> findByUsername(String username);
}
