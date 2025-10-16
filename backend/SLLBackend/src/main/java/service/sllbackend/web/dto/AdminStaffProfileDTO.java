package service.sllbackend.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.StaffStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStaffProfileDTO {
    private String name;

    @Email
    private String email;

    private StaffStatus staffStatus;

    private String socialSecurityNum;

    @PastOrPresent
    private LocalDate birthDate;
}
