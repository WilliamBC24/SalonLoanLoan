package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.UserAccount;

public interface UserAccountRepo extends JpaRepository<UserAccount, Long> {
    UserAccount findByUsername(String username);
}
