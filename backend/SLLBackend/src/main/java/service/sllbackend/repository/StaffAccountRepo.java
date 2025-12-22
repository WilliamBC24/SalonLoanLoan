package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.enumerator.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface StaffAccountRepo extends JpaRepository<StaffAccount, Long> {
    Optional<StaffAccount> findByUsername(String username);
    List<StaffAccount> findByUsernameContainingIgnoreCaseAndAccountStatus(String name, AccountStatus activeStatus);

    List<StaffAccount> findByUsernameContainingIgnoreCase(String name);

    List<StaffAccount> findByAccountStatus(AccountStatus activeStatus);

        @Query("""
        SELECT acc
        FROM StaffAccount acc
        JOIN FETCH acc.staff s
        WHERE acc.accountStatus = service.sllbackend.enumerator.AccountStatus.ACTIVE
          AND s.id IN :staffIds
        ORDER BY acc.staff.id
    """)
        List<StaffAccount> findAllActiveByStaffIds(@Param("staffIds") List<Integer> staffIds);
    }
