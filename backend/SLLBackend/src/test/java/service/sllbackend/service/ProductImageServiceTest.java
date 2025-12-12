package service.sllbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.ProductImage;
import service.sllbackend.repository.ProductImageRepo;
import service.sllbackend.repository.ProductRepo;
import service.sllbackend.service.impl.ProductImageServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private ProductImageRepo productImageRepo;

    @InjectMocks
    private ProductImageServiceImpl productImageService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1)
                .productName("Test Product")
                .currentPrice(100000)
                .productDescription("Test Description")
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

        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct));
        
        ProductImage mockImage = ProductImage.builder()
                .id(1)
                .product(testProduct)
                .imagePath("/path/to/image.jpg")
                .build();
        
        when(productImageRepo.save(any(ProductImage.class))).thenReturn(mockImage);

        // Act
        ProductImage result = productImageService.addImage(1, file);

        // Assert
        assertNotNull(result);
        assertEquals(mockImage.getId(), result.getId());
        assertEquals(mockImage.getImagePath(), result.getImagePath());
    }

    @Test
    void testAddImage_WithInvalidProductId_ShouldThrowException() {
        // Arrange
        byte[] imageContent = new byte[1024];
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                imageContent
        );

        when(productRepo.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            productImageService.addImage(999, file);
        });
    }

    @Test
    void testGetImages_ShouldReturnList() {
        // Arrange
        List<ProductImage> mockImages = new ArrayList<>();
        mockImages.add(ProductImage.builder()
                .id(1)
                .product(testProduct)
                .imagePath("/path/to/image1.jpg")
                .build());
        
        when(productImageRepo.findByProductId(1)).thenReturn(mockImages);

        // Act
        List<ProductImage> result = productImageService.getImages(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("/path/to/image1.jpg", result.get(0).getImagePath());
    }
}
