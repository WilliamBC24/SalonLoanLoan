package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.RequestedService;

import java.util.List;

public interface RequestedServiceRepo extends JpaRepository<RequestedService, Long> {
    List<RequestedService> findByAppointmentId(Integer appointmentId);
}
