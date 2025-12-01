package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.Appointment;
import service.sllbackend.enumerator.AppointmentStatus;

import java.util.List;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
    List<Appointment> findByNameIgnoreCaseContainingAndStatusIn(String name, List<AppointmentStatus> statuses);

    List<Appointment> findByNameIgnoreCaseContaining(String name);
}
