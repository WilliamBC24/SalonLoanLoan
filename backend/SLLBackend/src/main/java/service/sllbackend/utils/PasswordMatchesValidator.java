package service.sllbackend.utils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import service.sllbackend.utils.annotations.PasswordMatches;
import service.sllbackend.web.dto.PasswordChangeDTO;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordChangeDTO> {

    @Override
    public boolean isValid(PasswordChangeDTO dto, ConstraintValidatorContext context) {
        if (dto.getNewPassword() == null || dto.getConfirmPassword() == null) return false;
        return dto.getNewPassword().equals(dto.getConfirmPassword());
    }
}
