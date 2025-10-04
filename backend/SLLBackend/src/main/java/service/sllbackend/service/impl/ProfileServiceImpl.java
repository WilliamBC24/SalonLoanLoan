package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.repository.StaffRepo;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.service.ProfileService;
import service.sllbackend.utils.ProfileUtils;
import service.sllbackend.web.dto.StaffProfileDTO;
import service.sllbackend.web.dto.UserProfileDTO;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserAccountRepo userAccountRepo;
    private final StaffAccountRepo staffAccountRepo;
    private final StaffRepo staffRepo;
    private final ProfileUtils profileUtils;

    @Override
    @Transactional(readOnly = true)
    public UserAccount getCurrentUser(String username) {
        return userAccountRepo.findByUsername(username).orElse(null);
    }
    @Override
    @Transactional(readOnly = true)
    public UserAccount getCurrentUser(Long userId) {
        return userAccountRepo.findById(userId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffAccount getCurrentStaff(String username) {
        return staffAccountRepo.findByUsername(username).orElse(null);
    }

    @Override
    @Transactional
    public void updateUserProfile(Long userId, UserProfileDTO userProfileDTO) {
        UserAccount existingUser = userAccountRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean changed = false;

        profileUtils.validateUserProfile(userId, userProfileDTO.getUsername(), userProfileDTO.getEmail()
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
    public void updateStaffProfile(Long staffId, StaffProfileDTO staffProfileDTO) {
        Staff existingStaff = staffRepo.findById(staffId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));
        profileUtils.validateStaffProfile(staffId, staffProfileDTO.getName());

        existingStaff.setName(staffProfileDTO.getName().trim());
        staffRepo.save(existingStaff);
    }
}
