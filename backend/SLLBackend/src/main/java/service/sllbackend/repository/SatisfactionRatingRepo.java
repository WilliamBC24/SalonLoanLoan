package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.SatisfactionRating;

import java.util.Optional;

public interface SatisfactionRatingRepo extends JpaRepository<SatisfactionRating, Long> {
    Optional<SatisfactionRating> findByAppointment(Appointment appointment);
}
