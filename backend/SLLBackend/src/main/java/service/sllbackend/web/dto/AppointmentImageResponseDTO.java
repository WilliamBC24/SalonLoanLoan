package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentImageResponseDTO {
    
    private Integer id;
    private Integer appointmentId;
    private String imagePath;
    private String imageType; // "before" or "after"
}
