package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.ShiftTemplate;

public interface ShiftTemplateRepo extends JpaRepository<ShiftTemplate, Long> {
}
