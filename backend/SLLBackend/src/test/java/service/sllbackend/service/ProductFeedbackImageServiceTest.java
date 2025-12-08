package service.sllbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.ProductFeedback;
import service.sllbackend.entity.ProductFeedbackImage;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.ProductFeedbackImageRepo;
import service.sllbackend.repository.ProductFeedbackRepo;
import service.sllbackend.service.impl.ProductFeedbackImageServiceImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ProductFeedbackImageServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private ProductFeedbackRepo productFeedbackRepo;

    @Mock
    private ProductFeedbackImageRepo productFeedbackImageRepo;

    private ProductFeedbackImageServiceImpl productFeedbackImageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productFeedbackImageService = new ProductFeedbackImageServiceImpl(
                productFeedbackRepo,
                productFeedbackImageRepo
        );
    }

    @Test
    void testAddImage_Success() throws IOException {
        // Arrange
        Integer feedbackId = 1;
        byte[] imageContent = "test image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                imageContent
        );

        ProductFeedback feedback = ProductFeedback.builder()
                .id(feedbackId)
                .build();

        ProductFeedbackImage savedImage = ProductFeedbackImage.builder()
                .id(1)
                .productFeedback(feedback)
                .imagePath("uploads/product-feedback-images/product-feedback-123.jpg")
                .build();

        when(productFeedbackRepo.findById(feedbackId)).thenReturn(Optional.of(feedback));
        when(productFeedbackImageRepo.save(any(ProductFeedbackImage.class))).thenReturn(savedImage);

        // Act
        ProductFeedbackImage result = productFeedbackImageService.addImage(feedbackId, file);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(productFeedbackRepo).findById(feedbackId);
        verify(productFeedbackImageRepo).save(any(ProductFeedbackImage.class));
    }

    @Test
    void testAddImage_EmptyFile_ThrowsException() {
        // Arrange
        Integer feedbackId = 1;
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                new byte[0]
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                productFeedbackImageService.addImage(feedbackId, emptyFile)
        );
        verify(productFeedbackImageRepo, never()).save(any());
    }

    @Test
    void testAddImage_InvalidFileType_ThrowsException() {
        // Arrange
        Integer feedbackId = 1;
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "test content".getBytes()
        );

        ProductFeedback feedback = ProductFeedback.builder()
                .id(feedbackId)
                .build();

        when(productFeedbackRepo.findById(feedbackId)).thenReturn(Optional.of(feedback));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                productFeedbackImageService.addImage(feedbackId, invalidFile)
        );
        verify(productFeedbackImageRepo, never()).save(any());
    }

    @Test
    void testAddImage_FeedbackNotFound_ThrowsException() {
        // Arrange
        Integer feedbackId = 999;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        when(productFeedbackRepo.findById(feedbackId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                productFeedbackImageService.addImage(feedbackId, file)
        );
        verify(productFeedbackImageRepo, never()).save(any());
    }

    @Test
    void testGetImages() {
        // Arrange
        Integer feedbackId = 1;
        ProductFeedback feedback = ProductFeedback.builder()
                .id(feedbackId)
                .build();

        ProductFeedbackImage image1 = ProductFeedbackImage.builder()
                .id(1)
                .productFeedback(feedback)
                .imagePath("path1.jpg")
                .build();

        ProductFeedbackImage image2 = ProductFeedbackImage.builder()
                .id(2)
                .productFeedback(feedback)
                .imagePath("path2.jpg")
                .build();

        List<ProductFeedbackImage> images = List.of(image1, image2);
        when(productFeedbackImageRepo.findByProductFeedbackId(feedbackId)).thenReturn(images);

        // Act
        List<ProductFeedbackImage> result = productFeedbackImageService.getImages(feedbackId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productFeedbackImageRepo).findByProductFeedbackId(feedbackId);
    }
}
