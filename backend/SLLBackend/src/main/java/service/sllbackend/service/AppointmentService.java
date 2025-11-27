package service.sllbackend.service;

import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.AppointmentDetails;
import service.sllbackend.entity.RequestedService;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.web.dto.AppointmentRegisterDTO;

import java.util.List;

public interface AppointmentService {
    List<Appointment> getByNameAndStatus(String name,List<AppointmentStatus> status);
    Appointment findById(Long id);
    List<RequestedService> getRequestedServices(Integer appointmentId);
    AppointmentDetails getDetailsByAppointmentId(Integer appointmentId);
    void save(Appointment appointment);
    void register(AppointmentRegisterDTO appointmentRegisterDTO);

}
