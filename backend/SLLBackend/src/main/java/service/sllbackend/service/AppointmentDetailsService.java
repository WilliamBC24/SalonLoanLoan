package service.sllbackend.service;

import service.sllbackend.entity.AppointmentDetails;
import service.sllbackend.entity.UserAccount;

public interface AppointmentDetailsService {
    AppointmentDetails findByAppointmentId(long appointmentId);

    void save(AppointmentDetails appointmentDetails);

    long countByUser(UserAccount user);

}
