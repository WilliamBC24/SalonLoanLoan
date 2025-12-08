package service.sllbackend.service;

import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.AfterAppointmentImage;
import service.sllbackend.entity.BeforeAppointmentImage;

import java.io.IOException;
import java.util.List;

public interface AppointmentImageService {
    BeforeAppointmentImage addBeforeImage(Integer appointmentId, MultipartFile file) throws IOException;
    AfterAppointmentImage addAfterImage(Integer appointmentId, MultipartFile file) throws IOException;
    List<BeforeAppointmentImage> getBeforeImages(Integer appointmentId);
    List<AfterAppointmentImage> getAfterImages(Integer appointmentId);
}
