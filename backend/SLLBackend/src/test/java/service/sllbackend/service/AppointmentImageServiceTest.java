package service.sllbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.AfterAppointmentImage;
import service.sllbackend.entity.BeforeAppointmentImage;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.repository.AfterAppointmentImageRepo;
import service.sllbackend.repository.AppointmentRepo;
import service.sllbackend.repository.BeforeAppointmentImageRepo;
import service.sllbackend.service.impl.AppointmentImageServiceImpl;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentImageServiceTest {

    @Mock
    private AppointmentRepo appointmentRepo;

    @Mock
    private BeforeAppointmentImageRepo beforeAppointmentImageRepo;

    @Mock
    private AfterAppointmentImageRepo afterAppointmentImageRepo;

    @InjectMocks
    private AppointmentImageServiceImpl appointmentImageService;

    private Appointment completedAppointment;
    private Appointment pendingAppointment;

    @BeforeEach
    void setUp() {
        completedAppointment = Appointment.builder()
                .id(1)
                .name("Test Customer")
                .phoneNumber("0123456789")
                .status(AppointmentStatus.COMPLETED)
                .build();

        pendingAppointment = Appointment.builder()
                .id(2)
                .name("Test Customer 2")
                .phoneNumber("0987654321")
                .status(AppointmentStatus.PENDING)
                .build();
    }

    @Test
    void testAddBeforeImage_WithCompletedStatus_ShouldSucceed() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(appointmentRepo.findById(1L)).thenReturn(Optional.of(completedAppointment));
        
        BeforeAppointmentImage mockImage = BeforeAppointmentImage.builder()
                .id(1)
                .appointment(completedAppointment)
                .imagePath("/path/to/image.jpg")
                .build();
        
        when(beforeAppointmentImageRepo.save(any(BeforeAppointmentImage.class))).thenReturn(mockImage);

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            BeforeAppointmentImage result = appointmentImageService.addBeforeImage(1, file);
            assertNotNull(result);
        });
    }

    @Test
    void testAddBeforeImage_WithNonCompletedStatus_ShouldThrowException() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(appointmentRepo.findById(2L)).thenReturn(Optional.of(pendingAppointment));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentImageService.addBeforeImage(2, file)
        );

        assertTrue(exception.getMessage().contains("Images can only be added to appointments with COMPLETED status"));
        assertTrue(exception.getMessage().contains("PENDING"));
    }

    @Test
    void testAddAfterImage_WithCompletedStatus_ShouldSucceed() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(appointmentRepo.findById(1L)).thenReturn(Optional.of(completedAppointment));
        
        AfterAppointmentImage mockImage = AfterAppointmentImage.builder()
                .id(1)
                .appointment(completedAppointment)
                .imagePath("/path/to/image.jpg")
                .build();
        
        when(afterAppointmentImageRepo.save(any(AfterAppointmentImage.class))).thenReturn(mockImage);

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            AfterAppointmentImage result = appointmentImageService.addAfterImage(1, file);
            assertNotNull(result);
        });
    }

    @Test
    void testAddAfterImage_WithNonCompletedStatus_ShouldThrowException() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(appointmentRepo.findById(2L)).thenReturn(Optional.of(pendingAppointment));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> appointmentImageService.addAfterImage(2, file)
        );

        assertTrue(exception.getMessage().contains("Images can only be added to appointments with COMPLETED status"));
        assertTrue(exception.getMessage().contains("PENDING"));
    }

    @Test
    void testAddBeforeImage_WithNonExistentAppointment_ShouldThrowException() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(appointmentRepo.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> appointmentImageService.addBeforeImage(999, file)
        );

        assertTrue(exception.getMessage().contains("Appointment not found"));
    }
}
