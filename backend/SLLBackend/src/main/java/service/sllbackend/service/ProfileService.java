package service.sllbackend.service;

import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AccountStatus;
import service.sllbackend.web.dto.StaffProfileDTO;
import service.sllbackend.web.dto.UserProfileDTO;

import java.util.List;

public interface ProfileService {
    UserAccount getCurrentUser(String username);
    UserAccount getCurrentUser(Long userId);
    StaffAccount getCurrentStaff(String username);
    void updateUserProfile(Long userId, UserProfileDTO userProfileDTO);
    void updateStaffProfile(Long staffId, StaffProfileDTO staffProfileDTO);
    List<StaffAccount> getStaffAccount(String username, AccountStatus activeStatus);
    List<UserAccount> getUserAccount(String username, AccountStatus activeStatus);
}
