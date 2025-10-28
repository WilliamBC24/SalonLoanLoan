package service.sllbackend.web.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.AppointmentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDetailsEditDTO {
    @Pattern(regexp = "^(\\+?[0-9]{7,15})?$")
    private String phoneNumber;

    @FutureOrPresent
    private LocalDateTime scheduledAt;
    private AppointmentStatus status;
    private String username;

    @FutureOrPresent
    private LocalDateTime actualStart;
    @FutureOrPresent
    private LocalDateTime actualEnd;
}
