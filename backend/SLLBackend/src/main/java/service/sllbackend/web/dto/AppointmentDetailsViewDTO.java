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
public class AppointmentDetailsViewDTO {
    private SimpleUserDTO user;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
}
