package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.AfterAppointmentImage;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.BeforeAppointmentImage;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.repository.AfterAppointmentImageRepo;
import service.sllbackend.repository.AppointmentRepo;
import service.sllbackend.repository.BeforeAppointmentImageRepo;
import service.sllbackend.service.AppointmentImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentImageServiceImpl implements AppointmentImageService {
    
    private final AppointmentRepo appointmentRepo;
    private final BeforeAppointmentImageRepo beforeAppointmentImageRepo;
    private final AfterAppointmentImageRepo afterAppointmentImageRepo;
    
    private static final String UPLOAD_DIR = "uploads/appointment-images/";

    @Override
    @Transactional
    public BeforeAppointmentImage addBeforeImage(Integer appointmentId, MultipartFile file) throws IOException {
        validateAppointmentStatus(appointmentId);
        String imagePath = saveFile(file, "before");
        
        Appointment appointment = appointmentRepo.findById(appointmentId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));
        
        BeforeAppointmentImage image = BeforeAppointmentImage.builder()
                .appointment(appointment)
                .imagePath(imagePath)
                .build();
        
        return beforeAppointmentImageRepo.save(image);
    }

    @Override
    @Transactional
    public AfterAppointmentImage addAfterImage(Integer appointmentId, MultipartFile file) throws IOException {
        validateAppointmentStatus(appointmentId);
        String imagePath = saveFile(file, "after");
        
        Appointment appointment = appointmentRepo.findById(appointmentId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));
        
        AfterAppointmentImage image = AfterAppointmentImage.builder()
                .appointment(appointment)
                .imagePath(imagePath)
                .build();
        
        return afterAppointmentImageRepo.save(image);
    }

    @Override
    public List<BeforeAppointmentImage> getBeforeImages(Integer appointmentId) {
        return beforeAppointmentImageRepo.findByAppointmentId(appointmentId);
    }

    @Override
    public List<AfterAppointmentImage> getAfterImages(Integer appointmentId) {
        return afterAppointmentImageRepo.findByAppointmentId(appointmentId);
    }

    private void validateAppointmentStatus(Integer appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));
        
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Images can only be added to appointments with COMPLETED status. Current status: " + appointment.getStatus());
        }
    }

    private String saveFile(MultipartFile file, String imageType) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : "";
        String filename = imageType + "-" + UUID.randomUUID() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Saved {} image to: {}", imageType, filePath);
        return filePath.toString();
    }
}
