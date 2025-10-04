package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.service.impl.ProfileServiceImpl;
import service.sllbackend.web.dto.StaffProfileDTO;
import service.sllbackend.web.dto.UserProfileDTO;

import java.security.Principal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileServiceImpl profileService;

    @GetMapping("/user/profile")
    public String userProfile(Model model, Principal principal) {
        UserAccount userAccount = profileService.getCurrentUser(principal.getName());
        model.addAttribute("user", userAccount);
        return "user-profile";
    }

    @GetMapping("/user/profile/edit")
    public String editProfile(Model model, Principal principal) {
        UserAccount userAccount = profileService.getCurrentUser(principal.getName());
        model.addAttribute("user", userAccount);
        return "user-profile-edit";
    }

    @PostMapping("/user/profile/update")
    public String updateUserProfile(Model model, UserProfileDTO userProfileDTO, Principal principal) {
        Integer userId = profileService.getCurrentUser(principal.getName()).getId();
        profileService.updateUserProfile(Long.valueOf(userId), userProfileDTO);

        UserAccount updatedUser = profileService.getCurrentUser(userId.longValue());
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
        
        model.addAttribute("user", updatedUser);
        return "user-profile";
    }

    @GetMapping("/staff/profile")
    public String staffProfile(Model model, Principal principal) {
        StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());

        model.addAttribute("staffAccount", staffAccount);
        return "staff-profile";
    }

    @GetMapping("/staff/profile/edit")
    public String editStaffProfile(Model model, Principal principal) {
        StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
        model.addAttribute("staffAccount", staffAccount);
        return "staff-profile-edit";
    }

    @PostMapping("/staff/profile/update")
    public String updateStaffProfile(Model model, StaffProfileDTO staffProfileDTO, Principal principal) {
        Integer staffId = profileService.getCurrentStaff(principal.getName()).getId();
        profileService.updateStaffProfile(Long.valueOf(staffId), staffProfileDTO);
        StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
        model.addAttribute("staffAccount", staffAccount);
        return "staff-profile";
    }
}
