package service.sllbackend.service;

import service.sllbackend.entity.JobPosting;
import service.sllbackend.web.dto.JobPostingDTO;
import service.sllbackend.web.dto.JobPostingEditDTO;

import java.util.List;

public interface JobPostingService {
    List<JobPosting> findJobPosting(String title, String status);
    void createJobPosting(JobPostingDTO jobPostingDTO);
    JobPosting editJobPosting(Long id, JobPostingEditDTO jobPostingEditDTO);
}
