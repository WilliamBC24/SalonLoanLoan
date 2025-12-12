package service.sllbackend.service;

import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.ServiceImage;

import java.io.IOException;
import java.util.List;

public interface ServiceImageService {
    ServiceImage addImage(Integer serviceId, MultipartFile file) throws IOException;
    List<ServiceImage> getImages(Integer serviceId);
    ServiceImage getImageById(Integer imageId);
    void deleteImage(Integer imageId);
}
