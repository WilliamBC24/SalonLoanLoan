package service.sllbackend.service;

import service.sllbackend.entity.RequestedService;

import java.util.List;

public interface RequestedServicesService {
    void flattenAndSave(List<Integer> requestedServices, int appointmentId);
    List<RequestedService> findByAppointmentId(int appointmentId);
    void save(RequestedService requestedService);
    void delete(RequestedService requestedService);
}
