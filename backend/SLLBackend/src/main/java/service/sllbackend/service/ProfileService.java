package service.sllbackend.service;

import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AccountStatus;
import service.sllbackend.web.dto.*;

import java.util.List;

public interface ProfileService {
    UserAccount getCurrentUser(String username);
    UserAccount getCurrentUser(Long userId);
    StaffAccount getCurrentStaff(String username);
    void updateUserProfile(Long userId, UserProfileDTO userProfileDTO);
    void updateStaffProfile(Long staffId, StaffProfileDTO staffProfileDTO);
    void userPasswordChange(Long userId, PasswordChangeDTO passwordChangeDTO);
    void staffPasswordChange(String username, PasswordChangeDTO passwordChangeDTO);
    List<StaffAccount> getStaffAccount(String username, AccountStatus activeStatus);
    List<UserAccount> getUserAccount(String username, AccountStatus activeStatus);
    void adminUpdateUserAccount(String username, AdminUserProfileDTO adminUserProfileDTO);
    void adminUpdateStaffAccount(String username, AdminStaffProfileDTO adminStaffProfileDTO) throws Exception;
}
