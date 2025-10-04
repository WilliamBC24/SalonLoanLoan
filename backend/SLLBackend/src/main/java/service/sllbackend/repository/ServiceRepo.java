package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.Service;

public interface ServiceRepo extends JpaRepository<Service, Integer> {
}
