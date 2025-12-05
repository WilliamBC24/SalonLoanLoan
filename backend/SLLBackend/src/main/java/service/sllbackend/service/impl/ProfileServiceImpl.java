package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AccountStatus;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.repository.StaffRepo;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.service.ProfileService;
import service.sllbackend.utils.EncryptSSN;
import service.sllbackend.utils.ValidationUtils;
import service.sllbackend.web.dto.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserAccountRepo userAccountRepo;
    private final StaffAccountRepo staffAccountRepo;
    private final StaffRepo staffRepo;
    private final ValidationUtils validationUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserAccount getCurrentUser(String username) {
        return userAccountRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User account not found"));
    }
    @Override
    @Transactional(readOnly = true)
    public UserAccount getCurrentUser(Long userId) {
        return userAccountRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User account not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public StaffAccount getCurrentStaff(String username) {
        return staffAccountRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Staff account not found"));
    }

    @Override
    @Transactional
    public void updateUserProfile(Long userId, UserProfileDTO userProfileDTO) {
        UserAccount existingUser = userAccountRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean changed = false;

        validationUtils.validateUserProfile(userId, userProfileDTO.getUsername(), userProfileDTO.getEmail()
                , userProfileDTO.getPhoneNumber(), userProfileDTO.getBirthDate());

        if (StringUtils.hasText(userProfileDTO.getUsername()) &&
                !userProfileDTO.getUsername().equals(existingUser.getUsername())) {
            existingUser.setUsername(userProfileDTO.getUsername().trim());
            changed = true;
        }

        if (StringUtils.hasText(userProfileDTO.getEmail()) &&
                !userProfileDTO.getEmail().equals(existingUser.getEmail())) {
            existingUser.setEmail(userProfileDTO.getEmail().trim());
            changed = true;
        }

        if (StringUtils.hasText(userProfileDTO.getPhoneNumber()) &&
                !userProfileDTO.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
            existingUser.setPhoneNumber(userProfileDTO.getPhoneNumber().trim());
            changed = true;
        }

        if (userProfileDTO.getGender() != null &&
                userProfileDTO.getGender() != existingUser.getGender()) {
            existingUser.setGender(userProfileDTO.getGender());
            changed = true;
        }

        if (userProfileDTO.getBirthDate() != null &&
                !userProfileDTO.getBirthDate().equals(existingUser.getBirthDate())) {
            existingUser.setBirthDate(userProfileDTO.getBirthDate());
            changed = true;
        }

        if (changed) {
            userAccountRepo.save(existingUser);
        }
    }

    @Override
    @Transactional
    public void adminUpdateUserAccount(Long id, AdminUserProfileDTO adminUserProfileDTO) {
        UserAccount existingUser = userAccountRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean changed = false;

        validationUtils.validateUserProfile(Long.valueOf(existingUser.getId()), null, adminUserProfileDTO.getEmail(),
                adminUserProfileDTO.getPhoneNumber(), null);

        if (adminUserProfileDTO.getAccountStatus() != null &&
                adminUserProfileDTO.getAccountStatus() != existingUser.getAccountStatus()) {
            existingUser.setAccountStatus(adminUserProfileDTO.getAccountStatus());
            changed = true;
        }

        if (StringUtils.hasText(adminUserProfileDTO.getEmail()) &&
                !adminUserProfileDTO.getEmail().equals(existingUser.getEmail())) {
            existingUser.setEmail(adminUserProfileDTO.getEmail().trim());
            changed = true;
        }

        if (StringUtils.hasText(adminUserProfileDTO.getPhoneNumber()) &&
                !adminUserProfileDTO.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
            existingUser.setPhoneNumber(adminUserProfileDTO.getPhoneNumber().trim());
            changed = true;
        }

        if (changed) {
            userAccountRepo.save(existingUser);
        }
    }


    @Override
    @Transactional
    public void updateStaffProfile(Long staffId, StaffProfileDTO staffProfileDTO) throws Exception {
        Staff existingStaff = staffRepo.findById(staffId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
        validationUtils.validateStaffProfile(staffId, staffProfileDTO.getName());

        existingStaff.setName(staffProfileDTO.getName().trim());
        existingStaff.setEmail(staffProfileDTO.getEmail().trim());
        staffRepo.save(existingStaff);
    }

    @Override
    public void userPasswordChange(Long userId, PasswordChangeDTO passwordChangeDTO) {
        UserAccount currentUser = userAccountRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User account not found"));
        String currentPassword = currentUser.getPassword();
        passwordFilter(passwordChangeDTO, currentPassword);

        currentUser.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        userAccountRepo.save(currentUser);
    }

    @Override
    public void staffPasswordChange(String username, PasswordChangeDTO passwordChangeDTO) {
        StaffAccount currentStaff = staffAccountRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Staff account not found"));
        String currentPassword = currentStaff.getPassword();
        passwordFilter(passwordChangeDTO, currentPassword);

        currentStaff.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        staffAccountRepo.save(currentStaff);
    }

    private void passwordFilter(PasswordChangeDTO passwordChangeDTO, String password) {
        if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), password)) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        if (passwordEncoder.matches(passwordChangeDTO.getNewPassword(), password)) {
            throw new IllegalArgumentException("New password is the same as old password");
        }
    }

    @Override
    @Transactional
    public void adminUpdateStaffAccount(Long id, AdminStaffProfileDTO adminStaffProfileDTO) throws Exception {
        StaffAccount existingStaffAccount = staffAccountRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff account not found"));
        Staff staff = existingStaffAccount.getStaff();
        boolean changed = false;

        validationUtils.validateStaffProfileAdmin(Long.valueOf(existingStaffAccount.getId()), adminStaffProfileDTO.getName(),
                adminStaffProfileDTO.getEmail(), adminStaffProfileDTO.getSocialSecurityNum());
        if (StringUtils.hasText(adminStaffProfileDTO.getName()) && !adminStaffProfileDTO.getName().equals(staff.getName())) {
            staff.setName(adminStaffProfileDTO.getName().trim());
            changed = true;
        }
        if (StringUtils.hasText(adminStaffProfileDTO.getEmail()) && !adminStaffProfileDTO.getEmail().equals(staff.getEmail())) {
            staff.setEmail(adminStaffProfileDTO.getEmail().trim());
            changed = true;
        }
        if (adminStaffProfileDTO.getStaffStatus() != null &&
                adminStaffProfileDTO.getStaffStatus() != staff.getStaffStatus()) {
            staff.setStaffStatus(adminStaffProfileDTO.getStaffStatus());
            changed = true;
        }
        if (StringUtils.hasText(adminStaffProfileDTO.getSocialSecurityNum()) &&
                !adminStaffProfileDTO.getSocialSecurityNum().equals(staff.getSocialSecurityNum())) {
            String encryptedSSN = EncryptSSN.encrypt(adminStaffProfileDTO.getSocialSecurityNum().trim());
            staff.setSocialSecurityNum(encryptedSSN);
            changed = true;
        }
        if (adminStaffProfileDTO.getBirthDate() != null &&
                !adminStaffProfileDTO.getBirthDate().equals(staff.getBirthDate())) {
            staff.setBirthDate(adminStaffProfileDTO.getBirthDate());
            changed = true;
        }
        if (changed) {
            staffRepo.save(staff);
        }
    }

    @Override
    @Transactional
    public void adminCreateStaff(AdminCreateStaffDTO dto) throws Exception {
        Staff staff = new Staff();

        staff.setName(dto.getName().trim());
        staff.setBirthDate(dto.getBirthDate());
        staff.setEmail(dto.getEmail());

        if (org.springframework.util.StringUtils.hasText(dto.getSocialSecurityNum())) {
            String encrypted = EncryptSSN.encrypt(dto.getSocialSecurityNum().trim());
            staff.setSocialSecurityNum(encrypted);
        }

        staffRepo.save(staff);
    }


    @Override
    @Transactional(readOnly = true)
    public List<StaffAccount> getStaffAccount(String username, AccountStatus activeStatus) {
        if (username != null && !username.isBlank() && activeStatus != null) {
            return staffAccountRepo.findByUsernameContainingIgnoreCaseAndAccountStatus(username, activeStatus);
        } else if (username != null && !username.isBlank()) {
            return staffAccountRepo.findByUsernameContainingIgnoreCase(username);
        } else if (activeStatus != null) {
            return staffAccountRepo.findByAccountStatus(activeStatus);
        } else {
            return staffAccountRepo.findAll();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAccount> getUserAccount(String username, AccountStatus activeStatus) {
        if (username != null && !username.isBlank() && activeStatus != null) {
            return userAccountRepo.findByUsernameContainingIgnoreCaseAndAccountStatus(username, activeStatus);
        } else if (username != null && !username.isBlank()) {
            return userAccountRepo.findByUsernameContainingIgnoreCase(username);
        } else if (activeStatus != null) {
            return userAccountRepo.findByAccountStatus(activeStatus);
        } else {
            return userAccountRepo.findAll();
        }
    }
}
