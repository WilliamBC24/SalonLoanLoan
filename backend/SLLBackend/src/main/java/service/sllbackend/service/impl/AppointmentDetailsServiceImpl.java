package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.AppointmentDetails;
import service.sllbackend.repository.AppointmentDetailsRepo;
import service.sllbackend.service.AppointmentDetailsService;

@Service
@RequiredArgsConstructor
public class AppointmentDetailsServiceImpl implements AppointmentDetailsService {
    private final AppointmentDetailsRepo appointmentDetailsRepo;

    @Override
    @Transactional(readOnly = true)
    public AppointmentDetails findByAppointmentId(long appointmentId) {
        return appointmentDetailsRepo.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
    }

    @Override
    @Transactional
    public void save(AppointmentDetails appointmentDetails) {
        appointmentDetailsRepo.save(appointmentDetails);
    }
}
