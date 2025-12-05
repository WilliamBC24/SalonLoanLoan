package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.AfterAppointmentImage;

import java.util.List;

public interface AfterAppointmentImageRepo extends JpaRepository<AfterAppointmentImage, Integer> {
    List<AfterAppointmentImage> findByAppointmentId(Integer appointmentId);
}
