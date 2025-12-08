package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.AppointmentInvoice;

import java.util.Optional;

public interface AppointmentInvoiceRepo extends JpaRepository<AppointmentInvoice, Long> {
    Optional<AppointmentInvoice> findByAppointmentId(Integer appointmentId);
}
