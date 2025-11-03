package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AccountStatus;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.service.impl.ProfileServiceImpl;
import service.sllbackend.web.dto.AdminStaffProfileDTO;
import service.sllbackend.web.dto.AdminUserProfileDTO;

import java.util.List;

@Controller
@RequestMapping("/admin/profiles")
@RequiredArgsConstructor
public class AccountManagementController {
    private final ProfileServiceImpl profileService;
    private final UserAccountRepo userAccountRepo;
    private final StaffAccountRepo staffAccountRepo;

    @GetMapping("")
    public String profiles(@RequestParam(value = "username", required = false) String username,
                           @RequestParam(value = "activeStatus", required = false) AccountStatus activeStatus,
                           @RequestParam(value = "staffOnly", required = false) Boolean staffOnly,
                           Model model) {
        List<?> accounts;
        if (Boolean.TRUE.equals(staffOnly)) {
            accounts = profileService.getStaffAccount(username.trim(), activeStatus);
            model.addAttribute("type", "staff");
        } else {
            accounts = profileService.getUserAccount(username == null ? username : username.trim(), activeStatus);
            model.addAttribute("type", "user");
        }
        model.addAttribute("accounts", accounts);
        return "admin-profile-list";
    }

    @GetMapping("/user/edit/{id}")
    public String editUserAccount(@PathVariable String id, Model model) {
        UserAccount userAccount = userAccountRepo.findById(Long.valueOf(id))
                .orElseThrow(() -> new IllegalArgumentException("User account not found"));
        model.addAttribute("user", userAccount);
        return "admin-user-edit";
    }

    @PostMapping("/user/update/{id}")
    public String updateUserAccount(@PathVariable String id,
                                    @Valid @ModelAttribute AdminUserProfileDTO adminUserProfileDTO,
                                    BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            UserAccount userAccount = userAccountRepo.findById(Long.valueOf(id))
                    .orElseThrow(() -> new IllegalArgumentException("User account not found"));
            model.addAttribute("user", userAccount);
            return "admin-user-edit";
        }
        try {
            profileService.adminUpdateUserAccount(Long.valueOf(id), adminUserProfileDTO);
            return "redirect:/admin/profiles/user/edit/" + id + "?updated";
        } catch (Exception e) {
            return "redirect:/admin/profiles/user/edit/" + id + "?error";
        }
    }

    @GetMapping("/staff/edit/{id}")
    public String editStaffAccount(@PathVariable String id, Model model) {
        StaffAccount staffAccount = staffAccountRepo.findById(Long.valueOf(id))
                .orElseThrow(() -> new IllegalArgumentException("Staff account not found"));
        model.addAttribute("staffAccount", staffAccount);
        return "admin-staff-edit";
    }

    @PostMapping("/staff/update/{id}")
    public String updateStaffAccount(@PathVariable String id, @Valid @ModelAttribute AdminStaffProfileDTO adminStaffProfileDTO,
                                     BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            StaffAccount staffAccount = staffAccountRepo.findById(Long.valueOf(id))
                    .orElseThrow(() -> new IllegalArgumentException("Staff account not found"));
            model.addAttribute("staffAccount", staffAccount);
            return "admin-staff-edit";
        }
        try {
            profileService.adminUpdateStaffAccount(Long.valueOf(id), adminStaffProfileDTO);
            return "redirect:/admin/profiles/staff/edit/" + id + "?updated";
        } catch (Exception e) {
            return "redirect:/admin/profiles/staff/edit/" + id + "?error";
        }
    }
}
