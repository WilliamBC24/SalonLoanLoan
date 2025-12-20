package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.sllbackend.entity.JobPosting;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.JobPostingStatus;
import service.sllbackend.repository.JobPostingRepo;
import service.sllbackend.service.JobApplicationService;
import service.sllbackend.service.UserAccountService;
import service.sllbackend.web.dto.JobApplicationDTO;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobApplicationController {
    private final JobPostingRepo jobPostingRepo;
    private final JobApplicationService jobApplicationService;
    private final UserAccountService userAccountService;

    @GetMapping("/list")
    public String jobApplicationList(Model model){
        List<JobPosting> jobPostingList = jobPostingRepo.findActiveAndNonExpiredJobPostings(
            JobPostingStatus.ACTIVE, LocalDate.now());
        model.addAttribute("jobPostingList", jobPostingList);
        return "job-posting-list";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model, Principal principal){
        JobPosting job = jobPostingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found"));
        model.addAttribute("job", job);
        
        // Check if logged-in user has already applied
        if (principal != null) {
            UserAccount userAccount = userAccountService.findByUsername(principal.getName());
            if (userAccount != null && userAccount.getPhoneNumber() != null) {
                boolean hasApplied = jobApplicationService.hasAlreadyApplied(id, userAccount.getPhoneNumber());
                model.addAttribute("hasAlreadyApplied", hasApplied);
                model.addAttribute("userPhoneNumber", userAccount.getPhoneNumber());
            }
        }
        
        return "job-posting-details";
    }

    @PostMapping("/apply/{id}")
    public String apply(@PathVariable Long id, Model model, @Valid @ModelAttribute JobApplicationDTO jobApplicationDTO, 
                       BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if (bindingResult.hasErrors()) {
            JobPosting job = jobPostingRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job posting not found"));
            model.addAttribute("job", job);
            return "job-posting-details";
        }
        try {
            jobApplicationService.saveApplication(id, jobApplicationDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Application submitted successfully!");
            return "redirect:/job/list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/job/details/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while submitting your application. Please try again.");
            return "redirect:/job/details/" + id;
        }
    }
}
