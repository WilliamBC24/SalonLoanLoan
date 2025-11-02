package service.sllbackend.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.utils.PasswordMatches;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class PasswordChangeDTO {
    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
