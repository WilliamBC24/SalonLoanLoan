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
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.service.impl.ProfileServiceImpl;
import service.sllbackend.utils.ProfileMapper;
import service.sllbackend.web.dto.StaffProfileDTO;
import service.sllbackend.web.dto.StaffProfileViewDTO;
import service.sllbackend.web.dto.UserProfileDTO;
import service.sllbackend.web.dto.UserProfileViewDTO;

import java.security.Principal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileServiceImpl profileService;
    private final ProfileMapper profileMapper;

    @GetMapping("/user/profile")
    public String userProfile(Model model, Principal principal) {
        UserAccount userAccount = profileService.getCurrentUser(principal.getName());
        UserProfileViewDTO userProfileViewDTO = profileMapper.toUserProfileViewDTO(userAccount);
        model.addAttribute("user", userProfileViewDTO);
        return "user-profile";
    }

    @GetMapping("/user/profile/edit")
    public String editProfile(Model model, Principal principal) {
        UserAccount userAccount = profileService.getCurrentUser(principal.getName());
        UserProfileViewDTO userProfileViewDTO = profileMapper.toUserProfileViewDTO(userAccount);
        model.addAttribute("user", userProfileViewDTO);
        return "user-profile-edit";
    }

    @PostMapping("/user/profile/update")
    public String updateUserProfile(Model model, @Valid @ModelAttribute UserProfileDTO userProfileDTO,
                                    BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            UserAccount userAccount = profileService.getCurrentUser(principal.getName());
            UserProfileViewDTO userProfileViewDTO = profileMapper.toUserProfileViewDTO(userAccount);
            model.addAttribute("user", userProfileViewDTO);
            return "user-profile-edit";
        }
        try {
            Integer userId = profileService.getCurrentUser(principal.getName()).getId();
            profileService.updateUserProfile(Long.valueOf(userId), userProfileDTO);

            UserAccount updatedUser = profileService.getCurrentUser(userId.longValue());
            UserProfileViewDTO updatedUserProfileViewDTO = profileMapper.toUserProfileViewDTO(updatedUser);
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
            UserProfileViewDTO userProfileViewDTO = profileMapper.toUserProfileViewDTO(userAccount);
            model.addAttribute("user", userProfileViewDTO);
            return "redirect:/user/profile/edit";
        }
    }

    @GetMapping("/staff/profile")
    public String staffProfile(Model model, Principal principal) {
        StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
        StaffProfileViewDTO staffProfileViewDTO = profileMapper.toStaffProfileViewDTO(staffAccount);
        model.addAttribute("staffAccount", staffProfileViewDTO);
        return "staff-profile";
    }

    @GetMapping("/staff/profile/edit")
    public String editStaffProfile(Model model, Principal principal) {
        StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
        StaffProfileViewDTO staffProfileViewDTO = profileMapper.toStaffProfileViewDTO(staffAccount);
        model.addAttribute("staffAccount", staffProfileViewDTO);
        return "staff-profile-edit";
    }

    @PostMapping("/staff/profile/update")
    public String updateStaffProfile(Model model, @Valid @ModelAttribute StaffProfileDTO staffProfileDTO, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
            StaffProfileViewDTO staffProfileViewDTO = profileMapper.toStaffProfileViewDTO(staffAccount);
            model.addAttribute("staffAccount", staffProfileViewDTO);
            return "staff-profile";
        }
        try {
            Integer staffId = profileService.getCurrentStaff(principal.getName()).getId();
            profileService.updateStaffProfile(Long.valueOf(staffId), staffProfileDTO);
            StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
            StaffProfileViewDTO staffProfileViewDTO = profileMapper.toStaffProfileViewDTO(staffAccount);
            model.addAttribute("staffAccount", staffProfileViewDTO);
            return "redirect:/staff/profile?updated";
        } catch (Exception e) {
            StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
            StaffProfileViewDTO staffProfileViewDTO = profileMapper.toStaffProfileViewDTO(staffAccount);
            model.addAttribute("staffAccount", staffProfileViewDTO);
            return "redirect:/staff/profile/edit";
        }
    }
}
