package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.RequestedService;
import service.sllbackend.entity.Staff;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestedServiceRepo extends JpaRepository<RequestedService, Long> {
    List<RequestedService> findByAppointmentId(Integer appointmentId);

    @Query("SELECT rs FROM RequestedService rs WHERE rs.appointment.id = :appointmentId")
    List<RequestedService> findByAppointmentId(@Param("appointmentId") int appointmentId);

    List<RequestedService> findByResponsibleStaffAndAppointment_ScheduledAtBetween(
            Staff staff,
            LocalDateTime start,
            LocalDateTime end
    );
}
