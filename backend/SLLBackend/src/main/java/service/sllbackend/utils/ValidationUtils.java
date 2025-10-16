package service.sllbackend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.StaffRepo;
import service.sllbackend.repository.UserAccountRepo;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ValidationUtils {

    private final UserAccountRepo userAccountRepo;
    private final StaffRepo staffRepo;

    public void validateNewUser(String username, String email, String phoneNumber) {
        checkDuplicates(username, email, phoneNumber);
    }

    public void validateUserProfile(Long currentUserId, String username, String email, String phoneNumber, LocalDate birthDate) {
        validateBirthDate(birthDate);
        checkConflicts(currentUserId, username, email, phoneNumber);
    }

    public void validateStaffProfile(Long currentStaffId, String username) {
        checkStaffNameConflicts(currentStaffId, username);
    }

    public void validateStaffProfileAdmin(Long currentStaffId, String name, String email, String ssn) throws Exception {
        checkStaffProfileConflicts(currentStaffId, name, email, ssn);
    }

    private void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            return;
        }

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Invalid age: " + age);
        }
    }

    private void checkDuplicates(String username, String email, String phoneNumber) {
        if (StringUtils.hasText(username) && userAccountRepo.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (StringUtils.hasText(email) && userAccountRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email is already in use");
        }
        if (StringUtils.hasText(phoneNumber) && userAccountRepo.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number is already in use");
        }
    }

    private void checkConflicts(Long currentUserId, String username, String email, String phoneNumber) {
        List<UserAccount> conflicts = userAccountRepo.findConflicts(username, email, phoneNumber, currentUserId);

        for (UserAccount conflict : conflicts) {
            if (StringUtils.hasText(username) && username.equals(conflict.getUsername())) {
                throw new IllegalArgumentException("Username is already taken");
            }
            if (StringUtils.hasText(email) && email.equals(conflict.getEmail())) {
                throw new IllegalArgumentException("Email is already in use");
            }
            if (StringUtils.hasText(phoneNumber) && phoneNumber.equals(conflict.getPhoneNumber())) {
                throw new IllegalArgumentException("Phone number is already in use");
            }
        }
    }

    private void checkStaffNameConflicts(Long currentUserId, String name) {
        if (Boolean.TRUE.equals(staffRepo.existsByNameAndIdNot(name, currentUserId))) {
            throw new IllegalArgumentException("Staff name is already in use");
        }
    }

    private void checkStaffProfileConflicts(Long currentUserId, String name, String email, String ssn) throws Exception {
        String encryptedSSN = EncryptSSN.encrypt(ssn);
        List<Staff> conflicts = staffRepo.findConflicts(name, email, encryptedSSN, currentUserId);
        for (Staff conflict : conflicts) {
            if (StringUtils.hasText(name) && name.equals(conflict.getName())) {
                throw new IllegalArgumentException("Name is already taken");
            }
            if (StringUtils.hasText(email) && email.equals(conflict.getEmail())) {
                throw new IllegalArgumentException("Email is already in use");
            }
            if (StringUtils.hasText(ssn) && encryptedSSN.equals(conflict.getSocialSecurityNum())) {
                throw new IllegalArgumentException("SSN is already in use");
            }
        }
    }
}
