package service.sllbackend.web.mvc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.JobPosting;
import service.sllbackend.repository.JobPostingRepo;
import service.sllbackend.service.impl.JobPostingServiceImpl;
import service.sllbackend.web.dto.JobPostingDTO;
import service.sllbackend.web.dto.JobPostingEditDTO;

import java.util.List;

@Controller
@RequestMapping("/admin/job")
@RequiredArgsConstructor
public class JobPostingController {
    private final JobPostingServiceImpl jobPostingService;
    private final JobPostingRepo jobPostingRepo;

    @GetMapping("/list")
    public String jobPostingList(@RequestParam(value = "title", required = false) String title,
                                 @RequestParam(value = "status", required = false) String status,
                                 Model model){
        List<JobPosting> jobPostingResults = jobPostingService.findJobPosting(title, status);
        model.addAttribute("jobs", jobPostingResults);
        return "admin-job-posting-list";
    }

    @GetMapping("/create-form")
    public String jobPostingCreateForm(){
        return "admin-job-posting-create";
    }

    @PostMapping("/create")
    public String jobPostingCreate(Model model, @Valid @ModelAttribute JobPostingDTO jobPostingDTO, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "admin-job-posting-create";
        }
        try {
            jobPostingService.createJobPosting(jobPostingDTO);
            return "redirect:/admin/job/list";
        } catch (Exception e) {
            return "redirect:/admin/job/create-form";
        }
    }

    @GetMapping("/view/{id}")
    public String jobPostingView(@PathVariable Long id, Model model){
        JobPosting jobPosting = jobPostingRepo.findById(id).orElse(null);
        model.addAttribute("jobPosting", jobPosting);
        return "admin-job-posting-edit";
    }

    @PostMapping("/edit/{id}")
    public String jobPostingEdit(@PathVariable Long id, @Valid @ModelAttribute JobPostingEditDTO jobPostingEditDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()){
            JobPosting jobPosting = jobPostingRepo.findByJobPostingName((jobPostingEditDTO.getJobPostingName())).orElse(null);
            model.addAttribute("jobPosting", jobPosting);
            return "admin-job-posting-edit";
        }
        try {
            jobPostingService.editJobPosting(id, jobPostingEditDTO);
            return "redirect:/admin/job/list";
        } catch (Exception e) {
            return "redirect:/admin/job/view/" + id;
        }
    }
}