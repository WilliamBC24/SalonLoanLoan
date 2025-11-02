package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.JobPosting;
import service.sllbackend.enumerator.JobPostingStatus;
import service.sllbackend.repository.JobPostingRepo;
import service.sllbackend.service.impl.JobApplicationServiceImpl;
import service.sllbackend.web.dto.JobApplicationDTO;

import java.util.List;

@Controller
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobApplicationController {
    private final JobPostingRepo jobPostingRepo;
    private final JobApplicationServiceImpl jobApplicationService;

    @GetMapping("/list")
    public String jobApplicationList(Model model){
        List<JobPosting> jobPostingList = jobPostingRepo.findAllByStatus(JobPostingStatus.ACTIVE);
        model.addAttribute("jobPostingList", jobPostingList);
        return "job-posting-list";
    }

    @GetMapping("/details/{id}")
    public String details(@PathVariable Long id, Model model){
        JobPosting job = jobPostingRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found"));
        model.addAttribute("job", job);
        return "job-posting-details";
    }

    @PostMapping("/apply/{id}")
    public String apply(@PathVariable Long id, Model model, @Valid @ModelAttribute JobApplicationDTO jobApplicationDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            JobPosting job = jobPostingRepo.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Job posting not found"));
            model.addAttribute("job", job);
            return "job-posting-details";
        }
        try {
            jobApplicationService.saveApplication(id, jobApplicationDTO);
            return "redirect:/job/list";
        } catch (Exception e) {
            return "redirect:/job/details/" + id;
        }
    }
}
