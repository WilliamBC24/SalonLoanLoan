package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Integer appointmentId;

    private LocalDateTime scheduledAt;

    private Integer preferredStaffId;
    private String preferredStaffName;

    private Integer responsibleStaffId;
    private String responsibleStaffName;

    private Integer shiftInstanceId;
}
