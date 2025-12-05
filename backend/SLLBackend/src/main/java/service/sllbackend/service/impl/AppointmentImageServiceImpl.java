package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.AfterAppointmentImage;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.BeforeAppointmentImage;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.repository.AfterAppointmentImageRepo;
import service.sllbackend.repository.AppointmentRepo;
import service.sllbackend.repository.BeforeAppointmentImageRepo;
import service.sllbackend.service.AppointmentImageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentImageServiceImpl implements AppointmentImageService {
    
    private final AppointmentRepo appointmentRepo;
    private final BeforeAppointmentImageRepo beforeAppointmentImageRepo;
    private final AfterAppointmentImageRepo afterAppointmentImageRepo;
    
    @Override
    @Transactional
    public BeforeAppointmentImage addBeforeImage(Integer appointmentId, String imagePath) {
        Appointment appointment = getAppointmentAndValidateStatus(appointmentId);
        
        BeforeAppointmentImage image = BeforeAppointmentImage.builder()
                .appointment(appointment)
                .imagePath(imagePath)
                .build();
        
        return beforeAppointmentImageRepo.save(image);
    }
    
    @Override
    @Transactional
    public AfterAppointmentImage addAfterImage(Integer appointmentId, String imagePath) {
        Appointment appointment = getAppointmentAndValidateStatus(appointmentId);
        
        AfterAppointmentImage image = AfterAppointmentImage.builder()
                .appointment(appointment)
                .imagePath(imagePath)
                .build();
        
        return afterAppointmentImageRepo.save(image);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BeforeAppointmentImage> getBeforeImages(Integer appointmentId) {
        return beforeAppointmentImageRepo.findByAppointmentId(appointmentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AfterAppointmentImage> getAfterImages(Integer appointmentId) {
        return afterAppointmentImageRepo.findByAppointmentId(appointmentId);
    }
    
    @Override
    @Transactional
    public void deleteBeforeImage(Integer imageId) {
        beforeAppointmentImageRepo.deleteById(imageId);
    }
    
    @Override
    @Transactional
    public void deleteAfterImage(Integer imageId) {
        afterAppointmentImageRepo.deleteById(imageId);
    }
    
    private Appointment getAppointmentAndValidateStatus(Integer appointmentId) {
        Appointment appointment = appointmentRepo.findById(Long.valueOf(appointmentId))
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));
        
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Images can only be added to appointments with COMPLETED status. " +
                    "Current status: " + appointment.getStatus());
        }
        
        return appointment;
    }
}
