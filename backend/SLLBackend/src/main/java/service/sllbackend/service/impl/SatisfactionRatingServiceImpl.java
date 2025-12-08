package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.SatisfactionRating;
import service.sllbackend.repository.SatisfactionRatingRepo;
import service.sllbackend.service.AppointmentService;
import service.sllbackend.service.SatisfactionRatingService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SatisfactionRatingServiceImpl implements SatisfactionRatingService {
    private final AppointmentService appointmentService;
    private final SatisfactionRatingRepo satisfactionRatingRepo;

    @Override
    @Transactional(readOnly = true)
    public Optional<SatisfactionRating> findByAppointmentId(int appointmentId) {
        Appointment appointment = appointmentService.findById((long) appointmentId);
        return satisfactionRatingRepo.findByAppointment(appointment);
    }

    @Override
    @Transactional
    public void save(SatisfactionRating rating) {
        satisfactionRatingRepo.save(rating);
    }
}
