package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.BeforeAppointmentImage;

import java.util.List;

public interface BeforeAppointmentImageRepo extends JpaRepository<BeforeAppointmentImage, Integer> {
    List<BeforeAppointmentImage> findByAppointmentId(Integer appointmentId);
}
