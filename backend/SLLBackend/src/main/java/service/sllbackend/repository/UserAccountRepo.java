package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.UserAccount;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepo extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByPhoneNumber(String phoneNumber);
    @Query("""
    select u from UserAccount u 
    where (u.username = :username or u.email = :email or u.phoneNumber = :phoneNumber) 
    and u.id <> :currentUserId
    """)
    List<UserAccount> findConflicts(
            @Param("username") String username,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("currentUserId") Long currentUserId);
}
