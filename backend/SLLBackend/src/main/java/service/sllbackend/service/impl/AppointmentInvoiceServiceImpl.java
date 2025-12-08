package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.AppointmentInvoice;
import service.sllbackend.repository.AppointmentInvoiceRepo;
import service.sllbackend.service.AppointmentInvoiceService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentInvoiceServiceImpl implements AppointmentInvoiceService {
    private final AppointmentInvoiceRepo appointmentInvoiceRepo;

    @Override
    @Transactional
    public AppointmentInvoice save(AppointmentInvoice appointmentInvoice) {
        return appointmentInvoiceRepo.save(appointmentInvoice);
    }

    @Override
    public Optional<AppointmentInvoice> findByAppointmentId(int id) {
        return appointmentInvoiceRepo.findByAppointmentId(id);
    }
}
