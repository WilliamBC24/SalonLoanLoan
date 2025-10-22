package service.sllbackend.service;

import service.sllbackend.entity.AppointmentDetails;

public interface AppointmentDetailsService {
    AppointmentDetails findByAppointmentId(long appointmentId);

    void save(AppointmentDetails appointmentDetails);
}
