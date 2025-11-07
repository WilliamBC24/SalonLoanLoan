package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.InventoryConsignment;

public interface InventoryConsignmentRepo extends JpaRepository<InventoryConsignment, Integer> {
}
