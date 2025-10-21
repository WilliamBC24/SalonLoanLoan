package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.Loyalty;
import service.sllbackend.entity.UserAccount;

public interface LoyaltyRepo extends JpaRepository<Loyalty, Long> {
    Loyalty findByUser(UserAccount user);
}
