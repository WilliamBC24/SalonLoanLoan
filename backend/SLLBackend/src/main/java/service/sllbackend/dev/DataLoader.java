package service.sllbackend.dev;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AccountRole;
import service.sllbackend.enumerator.Gender;
import service.sllbackend.repository.UserAccountRepo;

@Component
@RequiredArgsConstructor
public class DataLoader {
    private final UserAccountRepo userAccountRepo;
    private final PasswordEncoder passwordEncoder;

    public void run(String... args) {
        registerUser("admin", "admin");
    }

    public void registerUser(String username, String rawPassword) {
        String hashedPassword = passwordEncoder.encode(rawPassword);
        userAccountRepo.save(UserAccount.builder()
                .username(username)
                .password(hashedPassword)
                .gender(Gender.MALE)
                .phoneNumber("0991991991")
                .role(AccountRole.ADMIN)
                .build());
    }
}