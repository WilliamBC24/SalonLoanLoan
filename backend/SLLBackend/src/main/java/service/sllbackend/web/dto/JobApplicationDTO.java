package service.sllbackend.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDTO {

    @NotNull
    private String applicantName;

    @NotNull
    @PastOrPresent
    private LocalDate applicantDob;

    @NotNull
    @Pattern(regexp = "^\\+?[0-9]{7,15}$")
    private String applicantPhoneNumber;

}
