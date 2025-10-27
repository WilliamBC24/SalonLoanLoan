package service.sllbackend.service;

import service.sllbackend.entity.Appointment;
import service.sllbackend.enumerator.AppointmentStatus;

import java.util.List;

public interface AppointmentService {
    List<Appointment> getByNameAndStatus(String name,List<AppointmentStatus> status);
    Appointment findById(Long id);
    void save(Appointment appointment);
}
