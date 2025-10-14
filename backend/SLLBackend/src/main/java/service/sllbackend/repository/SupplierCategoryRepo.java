package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.SupplierCategory;

public interface SupplierCategoryRepo extends JpaRepository<SupplierCategory, Integer> {
}
