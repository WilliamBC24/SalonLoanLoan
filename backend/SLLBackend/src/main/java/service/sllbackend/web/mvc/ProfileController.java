package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.Loyalty;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.service.impl.LoyaltyServiceImpl;
import service.sllbackend.service.impl.ProfileServiceImpl;
import service.sllbackend.utils.DTOMapper;
import service.sllbackend.web.dto.*;

import java.security.Principal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileServiceImpl profileService;
    private final LoyaltyServiceImpl loyaltyService;
    private final DTOMapper DTOMapper;

    @GetMapping("/user/profile")
    public String userProfile(Model model, Principal principal) {
        UserAccount userAccount = profileService.getCurrentUser(principal.getName());
        Loyalty loyalty = loyaltyService.findLoyaltyByUser(userAccount);
        UserProfileViewDTO userProfileViewDTO = DTOMapper.toUserProfileViewDTO(userAccount, loyalty);
        model.addAttribute("user", userProfileViewDTO);
        return "user-profile";
    }

    @GetMapping("/user/profile/edit")
    public String editProfile(Model model, Principal principal) {
        UserAccount userAccount = profileService.getCurrentUser(principal.getName());
        UserProfileEditDTO userProfileEditDTO = DTOMapper.toUserProfileEditDTO(userAccount);
        model.addAttribute("user", userProfileEditDTO);
        return "user-profile-edit";
    }

    @PostMapping("/user/profile/update")
    public String updateUserProfile(Model model, @Valid @ModelAttribute UserProfileDTO userProfileDTO,
                                    BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            UserAccount userAccount = profileService.getCurrentUser(principal.getName());
            UserProfileEditDTO userProfileEditDTO = DTOMapper.toUserProfileEditDTO(userAccount);
            model.addAttribute("user", userProfileEditDTO);
            return "user-profile-edit";
        }
        try {
            Integer userId = profileService.getCurrentUser(principal.getName()).getId();
            profileService.updateUserProfile(Long.valueOf(userId), userProfileDTO);

            UserAccount updatedUser = profileService.getCurrentUser(userId.longValue());
            Loyalty loyalty = loyaltyService.findLoyaltyByUser(updatedUser);
            UserProfileViewDTO updatedUserProfileViewDTO = DTOMapper.toUserProfileViewDTO(updatedUser, loyalty);
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            UserDetails newPrincipal = new org.springframework.security.core.userdetails.User(
                    updatedUser.getUsername(),
                    updatedUser.getPassword(),
                    existingAuth.getAuthorities()
            );

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    newPrincipal,
                    existingAuth.getCredentials(),
                    existingAuth.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            model.addAttribute("user", updatedUserProfileViewDTO);
            return "redirect:/user/profile?updated";
        } catch (Exception e) {
            UserAccount userAccount = profileService.getCurrentUser(principal.getName());
            UserProfileEditDTO userProfileEditDTO = DTOMapper.toUserProfileEditDTO(userAccount);
            model.addAttribute("user", userProfileEditDTO);
            return "redirect:/user/profile/edit";
        }
    }

    @GetMapping("/user/password")
    public String userPassword() {
        return "user-change-password";
    }

    @PostMapping("/user/password/change")
    public String userPasswordChange(@Valid @ModelAttribute PasswordChangeDTO passwordChangeDTO,
                                     BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "user-change-password";
        }
        try {
            long userId = profileService.getCurrentUser(principal.getName()).getId();
            profileService.userPasswordChange(userId, passwordChangeDTO);
            return "redirect:/user/profile";
        } catch (Exception e) {
            return "user-change-password";
        }
    }

    @GetMapping("/staff/profile")
    public String staffProfile(Model model, Principal principal) {
        StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
        StaffProfileViewDTO staffProfileViewDTO = DTOMapper.toStaffProfileViewDTO(staffAccount);
        model.addAttribute("staffAccount", staffProfileViewDTO);
        return "staff-profile";
    }

    @GetMapping("/staff/profile/edit")
    public String editStaffProfile(Model model, Principal principal) {
        StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
        StaffProfileViewDTO staffProfileViewDTO = DTOMapper.toStaffProfileViewDTO(staffAccount);
        model.addAttribute("staffAccount", staffProfileViewDTO);
        return "staff-profile-edit";
    }

    @PostMapping("/staff/profile/update")
    public String updateStaffProfile(Model model, @Valid @ModelAttribute StaffProfileDTO staffProfileDTO,
                                     BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
            StaffProfileViewDTO staffProfileViewDTO = DTOMapper.toStaffProfileViewDTO(staffAccount);
            model.addAttribute("staffAccount", staffProfileViewDTO);
            return "staff-profile";
        }
        try {
            Integer staffId = profileService.getCurrentStaff(principal.getName()).getId();
            profileService.updateStaffProfile(Long.valueOf(staffId), staffProfileDTO);
            StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
            StaffProfileViewDTO staffProfileViewDTO = DTOMapper.toStaffProfileViewDTO(staffAccount);
            model.addAttribute("staffAccount", staffProfileViewDTO);
            return "redirect:/staff/profile?updated";
        } catch (Exception e) {
            StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
            StaffProfileViewDTO staffProfileViewDTO = DTOMapper.toStaffProfileViewDTO(staffAccount);
            model.addAttribute("staffAccount", staffProfileViewDTO);
            return "redirect:/staff/profile/edit";
        }
    }

    @GetMapping("/staff/password")
    public String staffPassword() {
        return "staff-change-password";
    }

    @PostMapping("/staff/password/change")
    public String staffPasswordChange(@Valid @ModelAttribute PasswordChangeDTO passwordChangeDTO,
                                      BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "staff-change-password";
        }
        try {
            String username = profileService.getCurrentStaff(principal.getName()).getUsername();
            profileService.staffPasswordChange(username, passwordChangeDTO);
            return "redirect:/staff/profile";
        } catch (Exception e) {
            return "staff-change-password";
        }
    }
}
