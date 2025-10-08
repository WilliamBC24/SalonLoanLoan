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
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AccountStatus;
import service.sllbackend.service.impl.ProfileServiceImpl;
import service.sllbackend.web.dto.StaffProfileDTO;
import service.sllbackend.web.dto.UserProfileDTO;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileServiceImpl profileService;

    @GetMapping("/profiles")
    public String profiles(@RequestParam(value = "username", required = false) String username,
                           @RequestParam(value = "activeStatus", required = false) AccountStatus activeStatus,
                           @RequestParam(value = "staffOnly", required = false) Boolean staffOnly,
                           Model model) {
        List<?> accounts;
        if (Boolean.TRUE.equals(staffOnly)) {
            accounts = profileService.getStaffAccount(username, activeStatus);
        } else {
            accounts = profileService.getUserAccount(username, activeStatus);
        }
        model.addAttribute("accounts", accounts);
        return "staff-profile-list";
    }

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
    public String updateUserProfile(Model model, @Valid @ModelAttribute UserProfileDTO userProfileDTO,
                                    BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", profileService.getCurrentUser(principal.getName()));
            return "user-profile-edit";
        }
        try {
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
            return "redirect:/user/profile?updated";
        } catch (Exception e) {
            model.addAttribute("user", profileService.getCurrentUser(principal.getName()));
            return "redirect:/user/profile/edit";
        }
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
    public String updateStaffProfile(Model model, @Valid @ModelAttribute StaffProfileDTO staffProfileDTO, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("staffAccount", profileService.getCurrentStaff(principal.getName()));
            return "staff-profile";
        }
        try {
            Integer staffId = profileService.getCurrentStaff(principal.getName()).getId();
            profileService.updateStaffProfile(Long.valueOf(staffId), staffProfileDTO);
            StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
            model.addAttribute("staffAccount", staffAccount);
            return "redirect:/staff/profile?updated";
        } catch (Exception e) {
            model.addAttribute("staffAccount", profileService.getCurrentStaff(principal.getName()));
            return "redirect:/staff/profile/edit";
        }
    }
}
