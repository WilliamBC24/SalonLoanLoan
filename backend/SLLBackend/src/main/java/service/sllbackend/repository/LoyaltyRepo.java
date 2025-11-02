package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.Loyalty;
import service.sllbackend.entity.UserAccount;

import java.util.List;

public interface LoyaltyRepo extends JpaRepository<Loyalty, Long> {
    Loyalty findByUser(UserAccount user);

    @Query("SELECT l FROM Loyalty l JOIN l.user u WHERE (:username IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')))")
    List<Loyalty> findByUsernameContaining(@Param("username") String username);
}
