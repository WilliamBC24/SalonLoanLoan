package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.AppointmentDetails;

import java.util.Optional;

public interface AppointmentDetailsRepo extends JpaRepository<AppointmentDetails, Long> {
    Optional<AppointmentDetails> findByAppointmentId(Long appointmentId);
}
