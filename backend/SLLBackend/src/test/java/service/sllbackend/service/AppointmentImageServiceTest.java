package service.sllbackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.sllbackend.entity.AfterAppointmentImage;
import service.sllbackend.entity.Appointment;
import service.sllbackend.entity.BeforeAppointmentImage;
import service.sllbackend.enumerator.AppointmentStatus;
import service.sllbackend.repository.AfterAppointmentImageRepo;
import service.sllbackend.repository.AppointmentRepo;
import service.sllbackend.repository.BeforeAppointmentImageRepo;
import service.sllbackend.service.impl.AppointmentImageServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Nested
    @DisplayName("Add Before Image Tests")
    class AddBeforeImageTests {

        @Test
        @DisplayName("Should add before image when appointment is COMPLETED")
        void shouldAddBeforeImageWhenAppointmentIsCompleted() {
            // Given
            String imagePath = "/images/before/test.jpg";
            when(appointmentRepo.findById(1L)).thenReturn(Optional.of(completedAppointment));
            when(beforeAppointmentImageRepo.save(any(BeforeAppointmentImage.class)))
                    .thenAnswer(invocation -> {
                        BeforeAppointmentImage image = invocation.getArgument(0);
                        image.setId(1);
                        return image;
                    });

            // When
            BeforeAppointmentImage result = appointmentImageService.addBeforeImage(1, imagePath);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getImagePath()).isEqualTo(imagePath);
            assertThat(result.getAppointment()).isEqualTo(completedAppointment);
            verify(beforeAppointmentImageRepo).save(any(BeforeAppointmentImage.class));
        }

        @Test
        @DisplayName("Should throw IllegalStateException when appointment is not COMPLETED")
        void shouldThrowExceptionWhenAppointmentNotCompleted() {
            // Given
            String imagePath = "/images/before/test.jpg";
            when(appointmentRepo.findById(2L)).thenReturn(Optional.of(pendingAppointment));

            // When/Then
            assertThatThrownBy(() -> appointmentImageService.addBeforeImage(2, imagePath))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Images can only be added to appointments with COMPLETED status");

            verify(beforeAppointmentImageRepo, never()).save(any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when appointment not found")
        void shouldThrowExceptionWhenAppointmentNotFound() {
            // Given
            when(appointmentRepo.findById(99L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> appointmentImageService.addBeforeImage(99, "/images/test.jpg"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Appointment not found");

            verify(beforeAppointmentImageRepo, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Add After Image Tests")
    class AddAfterImageTests {

        @Test
        @DisplayName("Should add after image when appointment is COMPLETED")
        void shouldAddAfterImageWhenAppointmentIsCompleted() {
            // Given
            String imagePath = "/images/after/test.jpg";
            when(appointmentRepo.findById(1L)).thenReturn(Optional.of(completedAppointment));
            when(afterAppointmentImageRepo.save(any(AfterAppointmentImage.class)))
                    .thenAnswer(invocation -> {
                        AfterAppointmentImage image = invocation.getArgument(0);
                        image.setId(1);
                        return image;
                    });

            // When
            AfterAppointmentImage result = appointmentImageService.addAfterImage(1, imagePath);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getImagePath()).isEqualTo(imagePath);
            assertThat(result.getAppointment()).isEqualTo(completedAppointment);
            verify(afterAppointmentImageRepo).save(any(AfterAppointmentImage.class));
        }

        @Test
        @DisplayName("Should throw IllegalStateException when appointment is not COMPLETED")
        void shouldThrowExceptionWhenAppointmentNotCompleted() {
            // Given
            String imagePath = "/images/after/test.jpg";
            when(appointmentRepo.findById(2L)).thenReturn(Optional.of(pendingAppointment));

            // When/Then
            assertThatThrownBy(() -> appointmentImageService.addAfterImage(2, imagePath))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Images can only be added to appointments with COMPLETED status");

            verify(afterAppointmentImageRepo, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Images Tests")
    class GetImagesTests {

        @Test
        @DisplayName("Should get before images for appointment")
        void shouldGetBeforeImages() {
            // Given
            BeforeAppointmentImage image1 = BeforeAppointmentImage.builder()
                    .id(1)
                    .appointment(completedAppointment)
                    .imagePath("/images/before/1.jpg")
                    .build();
            BeforeAppointmentImage image2 = BeforeAppointmentImage.builder()
                    .id(2)
                    .appointment(completedAppointment)
                    .imagePath("/images/before/2.jpg")
                    .build();

            when(beforeAppointmentImageRepo.findByAppointmentId(1)).thenReturn(List.of(image1, image2));

            // When
            List<BeforeAppointmentImage> result = appointmentImageService.getBeforeImages(1);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(image1, image2);
        }

        @Test
        @DisplayName("Should get after images for appointment")
        void shouldGetAfterImages() {
            // Given
            AfterAppointmentImage image1 = AfterAppointmentImage.builder()
                    .id(1)
                    .appointment(completedAppointment)
                    .imagePath("/images/after/1.jpg")
                    .build();

            when(afterAppointmentImageRepo.findByAppointmentId(1)).thenReturn(List.of(image1));

            // When
            List<AfterAppointmentImage> result = appointmentImageService.getAfterImages(1);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getImagePath()).isEqualTo("/images/after/1.jpg");
        }
    }

    @Nested
    @DisplayName("Delete Images Tests")
    class DeleteImagesTests {

        @Test
        @DisplayName("Should delete before image")
        void shouldDeleteBeforeImage() {
            // When
            appointmentImageService.deleteBeforeImage(1);

            // Then
            verify(beforeAppointmentImageRepo).deleteById(1);
        }

        @Test
        @DisplayName("Should delete after image")
        void shouldDeleteAfterImage() {
            // When
            appointmentImageService.deleteAfterImage(1);

            // Then
            verify(afterAppointmentImageRepo).deleteById(1);
        }
    }
}
