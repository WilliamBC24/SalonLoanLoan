package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.ServiceImage;

import java.util.List;

public interface ServiceImageRepo extends JpaRepository<ServiceImage, Integer> {
    List<ServiceImage> findByServiceId(Integer serviceId);
}
