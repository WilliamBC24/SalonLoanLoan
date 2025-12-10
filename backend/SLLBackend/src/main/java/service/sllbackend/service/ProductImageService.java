package service.sllbackend.service;

import org.springframework.web.multipart.MultipartFile;
import service.sllbackend.entity.ProductImage;

import java.io.IOException;
import java.util.List;

public interface ProductImageService {
    ProductImage addImage(Integer productId, MultipartFile file) throws IOException;
    List<ProductImage> getImages(Integer productId);
    ProductImage getImageById(Integer imageId);
    void deleteImage(Integer imageId);
}
