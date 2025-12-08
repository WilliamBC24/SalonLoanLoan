package service.sllbackend.service;

import service.sllbackend.entity.AppointmentInvoice;

import java.util.Optional;

public interface AppointmentInvoiceService {
    AppointmentInvoice save(AppointmentInvoice appointmentInvoice);
    Optional<AppointmentInvoice> findByAppointmentId(int id);
}
