package service.sllbackend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.StaffRepo;
import service.sllbackend.repository.UserAccountRepo;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ProfileUtils {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{7,15}$");

    private final UserAccountRepo userAccountRepo;
    private final StaffRepo staffRepo;

    public void validateUserProfile(Long currentUserId, String username, String email, String phoneNumber, LocalDate birthDate) {
        validateUsername(username);
        validateEmail(email);
        validatePhone(phoneNumber);
        validateBirthDate(birthDate);
        checkConflicts(currentUserId, username, email, phoneNumber);
    }

    public void validateStaffProfile(Long currentStaffId, String username) {
        validateUsername(username);
        checkConflicts(currentStaffId, username);
    }

    private void validateUsername(String username) {
        if (!StringUtils.hasText(username)) return; // optional
        username = username.trim();
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Username must be 3-20 characters, letters, numbers, dots, underscores, or hyphens only");
        }
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email)) return; // optional
        email = email.trim();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validatePhone(String phoneNumber) {
        if (!StringUtils.hasText(phoneNumber)) return; // optional
        phoneNumber = phoneNumber.trim();
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    private void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) return;
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Invalid age: " + age);
        }
    }

    private void checkConflicts(Long currentUserId, String username, String email, String phoneNumber) {
        List<UserAccount> conflicts = userAccountRepo.findConflicts(username, email, phoneNumber, currentUserId);

        for (UserAccount conflict : conflicts) {
            if (username != null && username.equals(conflict.getUsername())) {
                throw new IllegalArgumentException("Username is already taken");
            }
            if (email != null && email.equals(conflict.getEmail())) {
                throw new IllegalArgumentException("Email is already in use");
            }
            if (phoneNumber != null && phoneNumber.equals(conflict.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number is already in use");
            }
        }
    }

    private void checkConflicts(Long currentUserId, String name) {
        if (staffRepo.existsByNameAndIdNot(name, currentUserId)) {
            throw new IllegalArgumentException("Staff name is already in use");
        }
    }
}
