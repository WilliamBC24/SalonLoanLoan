package service.sllbackend.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.utils.annotations.TimeInRange;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRegisterDTO {
    @NotEmpty
    private List<Integer> selectedServices;

    @NotNull
    @Future
    private LocalDate appointmentDate;

    @TimeInRange(start = "07:30", end = "19:00")
    private LocalTime appointmentTime;

    @NotNull
    @TimeInRange(start = "07:30", end = "19:30")
    private LocalTime endTime;

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{7,15}$")
    private String phoneNumber;
}
