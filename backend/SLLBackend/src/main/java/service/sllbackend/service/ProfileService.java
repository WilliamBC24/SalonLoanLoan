package service.sllbackend.service;

import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.web.dto.StaffProfileDTO;
import service.sllbackend.web.dto.UserProfileDTO;

public interface ProfileService {
    UserAccount getCurrentUser(String username);
    UserAccount getCurrentUser(Long userId);
    StaffAccount getCurrentStaff(String username);
    void updateUserProfile(Long userId, UserProfileDTO userProfileDTO);
    void updateStaffProfile(Long staffId, StaffProfileDTO staffProfileDTO);
}
