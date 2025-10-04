package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.ServiceCategory;

public interface ServiceCategoryRepo extends JpaRepository<ServiceCategory, Integer> {
}
