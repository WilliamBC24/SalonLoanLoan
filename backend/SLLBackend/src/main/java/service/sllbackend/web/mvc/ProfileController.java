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
import service.sllbackend.service.*;
import service.sllbackend.utils.DTOMapper;
import service.sllbackend.utils.EncryptSSN;
import service.sllbackend.web.dto.*;

import java.security.Principal;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final LoyaltyService loyaltyService;
    private final OrderService orderService;
    private final AppointmentDetailsService appointmentDetailsService;
    private final DTOMapper DTOMapper;

    @GetMapping("/user/profile")
    public String userProfile(Model model, Principal principal) {
        UserAccount userAccount = profileService.getCurrentUser(principal.getName());
        Loyalty loyalty = loyaltyService.findLoyaltyByUser(userAccount);
        UserProfileViewDTO userProfileViewDTO = DTOMapper.toUserProfileViewDTO(userAccount, loyalty);

        long orderCount = orderService.countByUser(userAccount);
        long appointmentCount = appointmentDetailsService.countByUser(userAccount);

        model.addAttribute("user", userProfileViewDTO);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("appointmentCount", appointmentCount);
        return "user-profile";
    }

    @GetMapping("/user/profile/edit")
    public String editProfile(Model model, Principal principal) {
        UserAccount userAccount = profileService.getCurrentUser(principal.getName());
        Loyalty loyalty = loyaltyService.findLoyaltyByUser(userAccount);
        UserProfileEditDTO userProfileEditDTO = DTOMapper.toUserProfileEditDTO(userAccount, loyalty);

        long orderCount = orderService.countByUser(userAccount);
        long appointmentCount = appointmentDetailsService.countByUser(userAccount);

        model.addAttribute("user", userProfileEditDTO);
        model.addAttribute("orderCount", orderCount);
        model.addAttribute("appointmentCount", appointmentCount);

        return "user-profile-edit";
    }

    @PostMapping("/user/profile/update")
    public String updateUserProfile(Model model, @Valid @ModelAttribute UserProfileDTO userProfileDTO,
                                    BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "redirect:/user/profile/edit?error";
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
            return "redirect:/user/profile/edit?error";        }
    }

    @GetMapping("/user/password")
    public String userPassword() {
        return "user-change-password";
    }

    @PostMapping("/user/password/change")
    public String userPasswordChange(@Valid @ModelAttribute PasswordChangeDTO passwordChangeDTO,
                                     BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "redirect:/user/password?error";
        }
        try {
            long userId = profileService.getCurrentUser(principal.getName()).getId();
            profileService.userPasswordChange(userId, passwordChangeDTO);
            return "redirect:/user/profile?updated";
        } catch (Exception e) {
            return "redirect:/user/password?error";
        }
    }

    @GetMapping("/staff/profile")
    public String staffProfile(Model model, Principal principal) throws Exception {
        StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
        StaffProfileViewDTO staffProfileViewDTO = DTOMapper.toStaffProfileViewDTO(staffAccount);
        if (staffAccount.getStaff().getSocialSecurityNum() != null) {
            staffProfileViewDTO.setSocialSecurityNum(EncryptSSN.decrypt(staffAccount.getStaff().getSocialSecurityNum()));
        }
        model.addAttribute("staffAccount", staffProfileViewDTO);
        return "staff-profile";
    }

    @GetMapping("/staff/profile/edit")
    public String editStaffProfile(Model model, Principal principal) throws Exception {
        StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
        StaffProfileViewDTO staffProfileViewDTO = DTOMapper.toStaffProfileViewDTO(staffAccount);
        if (staffAccount.getStaff().getSocialSecurityNum() != null) {
            staffProfileViewDTO.setSocialSecurityNum(EncryptSSN.decrypt(staffAccount.getStaff().getSocialSecurityNum()));
        }
        model.addAttribute("staffAccount", staffProfileViewDTO);
        return "staff-profile-edit";
    }

    @PostMapping("/staff/profile/update")
    public String updateStaffProfile(Model model, @Valid @ModelAttribute StaffProfileDTO staffProfileDTO,
                                     BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "redirect:/staff/profile/edit?error";
        }
        try {
            Integer staffId = profileService.getCurrentStaff(principal.getName()).getId();
            profileService.updateStaffProfile(Long.valueOf(staffId), staffProfileDTO);
            StaffAccount staffAccount = profileService.getCurrentStaff(principal.getName());
            StaffProfileViewDTO staffProfileViewDTO = DTOMapper.toStaffProfileViewDTO(staffAccount);
            model.addAttribute("staffAccount", staffProfileViewDTO);
            return "redirect:/staff/profile?updated";
        } catch (Exception e) {
            return "redirect:/staff/profile/edit?error";
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
