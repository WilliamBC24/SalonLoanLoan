package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.enumerator.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface StaffAccountRepo extends JpaRepository<StaffAccount, Long> {
    Optional<StaffAccount> findByUsername(String username);
    List<StaffAccount> findByUsernameContainingIgnoreCaseAndAccountStatus(String name, AccountStatus activeStatus);

    List<StaffAccount> findByUsernameContainingIgnoreCase(String name);

    List<StaffAccount> findByAccountStatus(AccountStatus activeStatus);
}
