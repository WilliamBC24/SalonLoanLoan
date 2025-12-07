package service.sllbackend.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateStaffDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Social security number is required")
    private String socialSecurityNum;

    @NotNull(message = "Birth date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @Email(message = "Invalid email format")
    private String email;
}
