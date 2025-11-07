package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.Appointment;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.repository.AppointmentRepo;
import service.sllbackend.service.AppointmentService;
import service.sllbackend.web.dto.AppointmentRegisterDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepo appointmentRepo;
    private final RequestedServicesServiceImpl requestedServicesService;

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getByNameAndStatus(String name, List<AppointmentStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return appointmentRepo.findByNameIgnoreCaseContaining(name != null ? name : "");
        } else {
            return appointmentRepo.findByNameIgnoreCaseContainingAndStatusIn(
                    name != null ? name : "", statuses
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment findById(Long id) {
        return appointmentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));
    }

    @Override
    @Transactional
    public void save(Appointment appointment) {
        appointmentRepo.save(appointment);
    }

    @Override
    @Transactional
    public void register(AppointmentRegisterDTO appointmentRegisterDTO) {
        long overlappingAppointments =
                appointmentRepo.concurrencyCheck(
                        appointmentRegisterDTO.getAppointmentTime().atDate(appointmentRegisterDTO.getAppointmentDate()),
                        appointmentRegisterDTO.getEndTime().atDate(appointmentRegisterDTO.getAppointmentDate()));
//        TODO: maxCap = staff * 3 - 2, and maybe loosen the overlapping rule
        if (overlappingAppointments > 10) {
            //TODO: update all other appointment statuses to REJECTED
            throw new IllegalArgumentException("Too many appointments");
        }

        Appointment savedAppointment = appointmentRepo.save(Appointment.builder()
                        .registeredAt(LocalDateTime.of(appointmentRegisterDTO.getAppointmentDate(), appointmentRegisterDTO.getAppointmentTime()))
                        .name(appointmentRegisterDTO.getName())
                        .phoneNumber(appointmentRegisterDTO.getPhoneNumber())
                .build());
        requestedServicesService.flattenAndSave(appointmentRegisterDTO.getSelectedServices(), savedAppointment.getId());
    }
}
