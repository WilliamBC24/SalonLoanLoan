package service.sllbackend.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(min = 7, max = 15, message = "SSN must be between 7 and 15 characters")
    @Pattern(regexp = "^[0-9]+$", message = "SSN must contain only digits")
    private String socialSecurityNum;

    @NotNull(message = "Birth date is required")
    @PastOrPresent(message = "Birth date cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @Email(message = "Invalid email format")
    private String email;
}
