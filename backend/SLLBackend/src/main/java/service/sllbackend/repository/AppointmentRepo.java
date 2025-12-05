package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import service.sllbackend.entity.Appointment;
import service.sllbackend.enumerator.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
    @Query("""
    select a from Appointment a join AppointmentDetails ad on a.id = ad.appointment.id and ad.user.id = ?1
    and a.status in ?2
""")
    List<Appointment> findByUserIdIgnoreCaseContainingAndStatusIn(int userAccountId, List<AppointmentStatus> statuses);
    List<Appointment> findByNameIgnoreCaseContainingAndStatusIn(String name, List<AppointmentStatus> statuses);

    @Query("""
    select a from Appointment a join AppointmentDetails ad on a.id = ad.appointment.id and ad.user.id = ?1
""")
    List<Appointment> findByUserIdIgnoreCaseContaining(int userAccountId);
    List<Appointment> findByNameIgnoreCaseContaining(String name);
    List<Appointment> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
}
