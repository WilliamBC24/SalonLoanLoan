package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.AppointmentInvoice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentInvoiceRepo extends JpaRepository<AppointmentInvoice, Long> {
    Optional<AppointmentInvoice> findByAppointmentId(Integer appointmentId);
    List<AppointmentInvoice> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
