package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.sllbackend.entity.JobPostingApplication;
import service.sllbackend.repository.JobPostingApplicationRepo;
import service.sllbackend.service.impl.JobApplicationServiceImpl;

import java.util.List;

@Controller
@RequestMapping("/staff/job")
@RequiredArgsConstructor
public class StaffJobApplicationController {
    private final JobPostingApplicationRepo jobPostingApplicationRepo;
    private final JobApplicationServiceImpl jobApplicationService;

    @GetMapping("/list")
    public String staffJobApplicationList(Model model){
        List<JobPostingApplication> applications = jobPostingApplicationRepo.findAll();
        model.addAttribute("applications", applications);
        return "staff-view-application-list";
    }

    @PostMapping("/accept/{id}")
    public String staffJobApplicationAccept(@PathVariable("id") Long id){
        jobApplicationService.acceptApplication(id);
        return "redirect:/staff/job/list";
    }

    @PostMapping("/reject/{id}")
    public String staffJobApplicationReject(@PathVariable("id") Long id){
        jobApplicationService.rejectApplication(id);
        return "redirect:/staff/job/list";
    }
}
