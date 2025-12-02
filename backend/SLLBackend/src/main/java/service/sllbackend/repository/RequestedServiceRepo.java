package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.RequestedService;

import java.util.List;

public interface RequestedServiceRepo extends JpaRepository<RequestedService, Long> {
    List<RequestedService> findByAppointmentId(Integer appointmentId);

    @Query("SELECT rs FROM RequestedService rs WHERE rs.appointment.id = :appointmentId")
    List<RequestedService> findByAppointmentId(@Param("appointmentId") int appointmentId);
}
