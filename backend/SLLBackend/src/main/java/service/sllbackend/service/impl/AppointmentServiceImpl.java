package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.Appointment;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.repository.AppointmentRepo;
import service.sllbackend.service.AppointmentService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepo appointmentRepo;

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


}
