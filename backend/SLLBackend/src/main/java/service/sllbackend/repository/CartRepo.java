package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.Cart;
import service.sllbackend.entity.UserAccount;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepo extends JpaRepository<Cart, Cart.CartId> {
    List<Cart> findByUserAccount(UserAccount userAccount);
    Optional<Cart> findByUserAccountAndProduct_Id(UserAccount userAccount, Integer productId);
}
