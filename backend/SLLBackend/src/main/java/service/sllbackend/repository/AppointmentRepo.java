package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.Appointment;
import service.sllbackend.enumerator.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
    List<Appointment> findByNameIgnoreCaseContainingAndStatusIn(String name, List<AppointmentStatus> statuses);

    List<Appointment> findByNameIgnoreCaseContaining(String name);

    @Query("""
    SELECT COUNT(ad.id) FROM AppointmentDetails ad WHERE ad.scheduledStart < :endTime AND ad.scheduledEnd > :startTime
""")
    Long concurrencyCheck(@Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime);
}
