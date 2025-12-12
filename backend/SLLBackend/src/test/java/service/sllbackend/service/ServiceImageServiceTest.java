package service.sllbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceCategory;
import service.sllbackend.entity.ServiceImage;
import service.sllbackend.enumerator.ServiceType;
import service.sllbackend.repository.ServiceImageRepo;
import service.sllbackend.repository.ServiceRepo;
import service.sllbackend.service.impl.ServiceImageServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceImageServiceTest {

    @Mock
    private ServiceRepo serviceRepo;

    @Mock
    private ServiceImageRepo serviceImageRepo;

    @InjectMocks
    private ServiceImageServiceImpl serviceImageService;

    private Service testService;

    @BeforeEach
    void setUp() {
        ServiceCategory category = ServiceCategory.builder()
                .id(1)
                .name("Hair Care")
                .build();
                
        testService = Service.builder()
                .id(1)
                .serviceName("Test Service")
                .serviceCategory(category)
                .serviceType(ServiceType.SINGLE)
                .servicePrice(100000)
                .durationMinutes((short) 60)
                .serviceDescription("Test Description")
                .activeStatus(true)
                .build();
    }

    @Test
    void testAddImage_WithValidFile_ShouldSucceed() throws IOException {
        // Arrange
        byte[] imageContent = new byte[1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                imageContent
        );

        when(serviceRepo.findById(1)).thenReturn(Optional.of(testService));
        
        ServiceImage mockImage = ServiceImage.builder()
                .id(1)
                .service(testService)
                .imagePath("/path/to/image.jpg")
                .build();
        
        when(serviceImageRepo.save(any(ServiceImage.class))).thenReturn(mockImage);

        // Act
        ServiceImage result = serviceImageService.addImage(1, file);

        // Assert
        assertNotNull(result);
        assertEquals(mockImage.getId(), result.getId());
        assertEquals(mockImage.getImagePath(), result.getImagePath());
    }

    @Test
    void testAddImage_WithInvalidServiceId_ShouldThrowException() {
        // Arrange
        byte[] imageContent = new byte[1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                imageContent
        );

        when(serviceRepo.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            serviceImageService.addImage(999, file);
        });
    }

    @Test
    void testGetImages_ShouldReturnList() {
        // Arrange
        List<ServiceImage> mockImages = new ArrayList<>();
        mockImages.add(ServiceImage.builder()
                .id(1)
                .service(testService)
                .imagePath("/path/to/image1.jpg")
                .build());
        
        when(serviceImageRepo.findByServiceId(1)).thenReturn(mockImages);

        // Act
        List<ServiceImage> result = serviceImageService.getImages(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("/path/to/image1.jpg", result.get(0).getImagePath());
    }
}
