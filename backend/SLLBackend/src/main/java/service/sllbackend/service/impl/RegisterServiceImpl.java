package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.service.RegisterService;
import service.sllbackend.utils.ValidationUtils;
import service.sllbackend.web.dto.UserRegisterDTO;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {
    private final ValidationUtils validationUtils;
    private final UserAccountRepo userAccountRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void registerUser(UserRegisterDTO userRegisterDTO) {
        validationUtils.validateNewUser(userRegisterDTO.getUsername(),
                userRegisterDTO.getEmail(), userRegisterDTO.getPhoneNumber());

        String hashedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
        userAccountRepo.save(UserAccount.builder()
                .username(userRegisterDTO.getUsername())
                .password(hashedPassword)
                .gender(userRegisterDTO.getGender())
                .birthDate(userRegisterDTO.getBirthDate() != null ? userRegisterDTO.getBirthDate() : null)
                .phoneNumber(userRegisterDTO.getPhoneNumber())
                .email(userRegisterDTO.getEmail() != null ? userRegisterDTO.getEmail() : null)
                .build());
    }
}
