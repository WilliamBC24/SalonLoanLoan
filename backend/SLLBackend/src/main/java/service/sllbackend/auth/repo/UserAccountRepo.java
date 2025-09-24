package service.sllbackend.auth.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.auth.entity.UserAccount;

public interface UserAccountRepo extends JpaRepository<UserAccount, Long> {
}
