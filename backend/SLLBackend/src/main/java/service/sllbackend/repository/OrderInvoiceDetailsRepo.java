package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.OrderInvoice;
import service.sllbackend.entity.OrderInvoiceDetails;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.OrderStatus;

import java.util.List;

@Repository
public interface OrderInvoiceDetailsRepo extends JpaRepository<OrderInvoiceDetails, Integer> {
    List<OrderInvoiceDetails> findByOrderInvoice(OrderInvoice orderInvoice);
    
    List<OrderInvoiceDetails> findByOrderInvoice_Id(Integer orderInvoiceId);
    
    @Query("SELECT CASE WHEN COUNT(oid) > 0 THEN true ELSE false END " +
           "FROM OrderInvoiceDetails oid " +
           "JOIN oid.orderInvoice oi " +
           "WHERE oi.userAccount = :userAccount " +
           "AND oi.orderStatus = :status " +
           "AND oid.product.id = :productId")
    boolean existsByUserAccountAndProductIdAndOrderStatus(
            @Param("userAccount") UserAccount userAccount,
            @Param("productId") Integer productId,
            @Param("status") OrderStatus status);
}
