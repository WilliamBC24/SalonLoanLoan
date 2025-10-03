package service.sllbackend.dev;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.*;
import service.sllbackend.enumerator.Gender;
import service.sllbackend.repository.*;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataLoader
//        implements CommandLineRunner
{
    private final UserAccountRepo userAccountRepo;
    private final StaffRepo staffRepo;
    private final StaffAccountRepo staffAccountRepo;
    private final StaffPositionRepo staffPositionRepo;
    private final StaffCurrentPositionRepo staffCurrentPositionRepo;
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

    public void registerStaff(String username, String rawPassword, String role, String name) {
        Staff staff = staffRepo.save(Staff.builder()
                .name(name)
                .birthDate(LocalDate.of(2004, 1, 1))
                .build());
        StaffPosition staffPosition = staffPositionRepo.save(StaffPosition.builder()
                .positionName(role)
                .build());
        staffCurrentPositionRepo.save(StaffCurrentPosition.builder()
                .staff(staff)
                .position(staffPosition)
                .build());
        String hashedPassword = passwordEncoder.encode(rawPassword);
        staffAccountRepo.save(StaffAccount.builder()
                .staff(staff)
                .username(username)
                .password(hashedPassword)
                .activeStatus(Boolean.TRUE)
                .build());
    }
}