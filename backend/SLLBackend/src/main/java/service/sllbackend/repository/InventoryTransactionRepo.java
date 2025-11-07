package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.InventoryTransaction;

public interface InventoryTransactionRepo extends JpaRepository<InventoryTransaction, Integer> {
}
