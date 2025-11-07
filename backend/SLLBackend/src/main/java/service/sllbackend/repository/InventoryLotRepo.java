package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.InventoryLot;
import service.sllbackend.entity.Product;

import java.util.List;

public interface InventoryLotRepo extends JpaRepository<InventoryLot, Integer> {
    
    List<InventoryLot> findByProduct(Product product);
    
    @Query("SELECT COALESCE(SUM(il.availableQuantity), 0) FROM InventoryLot il WHERE il.product.id = :productId")
    Integer getTotalAvailableStock(@Param("productId") Integer productId);
    
    @Query("SELECT il FROM InventoryLot il WHERE il.product.id = :productId AND il.availableQuantity > 0 ORDER BY il.productExpiryDate ASC")
    List<InventoryLot> findAvailableLotsByProductOrderByExpiryDate(@Param("productId") Integer productId);
}
