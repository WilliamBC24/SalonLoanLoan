package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.AppointmentDetails;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.AppointmentDetailsRepo;
import service.sllbackend.service.AppointmentDetailsService;

@Service
@RequiredArgsConstructor
public class AppointmentDetailsServiceImpl implements AppointmentDetailsService {
    private final AppointmentDetailsRepo appointmentDetailsRepo;

    @Override
    @Transactional(readOnly = true)
    public AppointmentDetails findByAppointmentId(long appointmentId) {
        return appointmentDetailsRepo.findByAppointmentId(appointmentId).orElse(new AppointmentDetails());
    }

    @Override
    @Transactional
    public void save(AppointmentDetails appointmentDetails) {
        appointmentDetailsRepo.save(appointmentDetails);
    }

    @Override
    public long countByUser(UserAccount user) {
        return appointmentDetailsRepo.countByUser(user);
    }
}
