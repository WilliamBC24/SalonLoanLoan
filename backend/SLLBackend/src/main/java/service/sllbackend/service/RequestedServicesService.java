package service.sllbackend.service;

import java.util.List;

public interface RequestedServicesService {
    void flattenAndSave(List<Integer> requestedServices, int appointmentId);
}
