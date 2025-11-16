package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.ProductFeedback;
import service.sllbackend.entity.UserAccount;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductFeedbackRepo extends JpaRepository<ProductFeedback, Integer> {
    List<ProductFeedback> findByProductOrderByIdDesc(Product product);
    Optional<ProductFeedback> findByUserAccountAndProduct(UserAccount userAccount, Product product);
    boolean existsByUserAccountAndProduct(UserAccount userAccount, Product product);
    List<ProductFeedback> findByUserAccount(UserAccount userAccount);
}
