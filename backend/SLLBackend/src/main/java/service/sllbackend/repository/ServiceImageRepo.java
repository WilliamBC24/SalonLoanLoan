package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceImage;

import java.util.List;

@Repository
public interface ServiceImageRepo extends JpaRepository<ServiceImage, Integer> {
    List<ServiceImage> findByService(Service service);
    List<ServiceImage> findByServiceId(Integer serviceId);
    void deleteByService(Service service);
}
