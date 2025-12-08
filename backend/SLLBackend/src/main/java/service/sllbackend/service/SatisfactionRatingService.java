package service.sllbackend.service;

import service.sllbackend.entity.SatisfactionRating;

import java.util.Optional;

public interface SatisfactionRatingService {
    Optional<SatisfactionRating> findByAppointmentId(int appointmentId);
    void save(SatisfactionRating rating);
}
