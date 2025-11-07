package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.RequestedService;
import service.sllbackend.entity.ServiceCombo;
import service.sllbackend.enumerator.ServiceType;
import service.sllbackend.repository.AppointmentRepo;
import service.sllbackend.repository.RequestedServiceRepo;
import service.sllbackend.repository.ServiceComboRepo;
import service.sllbackend.repository.ServiceRepo;
import service.sllbackend.service.RequestedServicesService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestedServicesServiceImpl implements RequestedServicesService {
    private final AppointmentRepo appointmentRepo;
    private final ServiceRepo serviceRepo;
    private final ServiceComboRepo serviceComboRepo;
    private final RequestedServiceRepo requestedServiceRepo;

    @Override
    @Transactional
    public void flattenAndSave(List<Integer> requestedServices, int appointmentId) {
        Appointment appointment = appointmentRepo.findById((long) appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("No appointment found with id: " + appointmentId));
        List<service.sllbackend.entity.Service> serviceList = serviceRepo.findAllById(requestedServices);
        List<RequestedService> toSave = new ArrayList<>();
        for (service.sllbackend.entity.Service service : serviceList) {
            if (service.getServiceType() == ServiceType.COMBO) {
                toSave.add(RequestedService.builder()
                        .appointment(appointment)
                        .service(service)
                        .priceAtBooking(service.getServicePrice())
                        .build());

                List<ServiceCombo> comboFlattened = serviceComboRepo.findByComboId(service.getId());
                for (ServiceCombo serviceCombo : comboFlattened) {
                    toSave.add(RequestedService.builder()
                                    .appointment(appointment)
                                    .service(serviceCombo.getService())
                                    .priceAtBooking(0)
                            .build());
                }
            } else {
                toSave.add(RequestedService.builder()
                                .appointment(appointment)
                                .service(service)
                                .priceAtBooking(service.getServicePrice())
                        .build());
            }
        }
        requestedServiceRepo.saveAll(toSave);
    }
}
