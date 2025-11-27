package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.entity.UserAccount;

import java.util.List;

@Repository
public interface OrderInvoiceRepo extends JpaRepository<OrderInvoice, Integer> {
    List<OrderInvoice> findByUserAccountOrderByCreatedAtDesc(UserAccount userAccount);

    long countByUserAccount(UserAccount user);
}
