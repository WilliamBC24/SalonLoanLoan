package service.sllbackend.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentImageDTO {
    
    @NotNull(message = "Appointment ID is required")
    private Integer appointmentId;
    
    @NotBlank(message = "Image path is required")
    private String imagePath;
}
