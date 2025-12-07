package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.JobPostingApplication;
import service.sllbackend.repository.JobPostingApplicationRepo;
import service.sllbackend.service.JobApplicationService;

import java.util.List;

@Controller
@RequestMapping("/manager/job-application")
@RequiredArgsConstructor
public class ManagerJobApplicationController {
    private final JobPostingApplicationRepo jobPostingApplicationRepo;
    private final JobApplicationService jobApplicationService;

    @GetMapping("/list")
    public String adminJobApplicationList(Model model){
        List<JobPostingApplication> applications = jobPostingApplicationRepo.findAll();
        model.addAttribute("applications", applications);
        return "manager-view-application-list";
    }

    @PostMapping("/accept/{id}")
    public String adminJobApplicationAccept(@PathVariable("id") Long id){
        jobApplicationService.acceptApplication(id);
        return "redirect:/manager/job-application/list";
    }

    @PostMapping("/reject/{id}")
    public String adminJobApplicationReject(@PathVariable("id") Long id){
        jobApplicationService.rejectApplication(id);
        return "redirect:/manager/job-application/list";
    }
}
