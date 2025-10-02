package service.sllbackend.dev;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.Gender;
import service.sllbackend.repository.UserAccountRepo;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataLoader
//        implements CommandLineRunner
{
    private final UserAccountRepo userAccountRepo;
    private final PasswordEncoder passwordEncoder;

//    @Override
    public void run(String... args) {
        registerUser("admin", "admin");
    }

    public void registerUser(String username, String rawPassword) {
        String hashedPassword = passwordEncoder.encode(rawPassword);
        userAccountRepo.save(UserAccount.builder()
                .username(username)
                .password(hashedPassword)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(2004,9,6))
                .phoneNumber("0991991991")
                .email("admin@admin.com")
                .build());
    }
}