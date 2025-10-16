package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.JobPosting;
import service.sllbackend.repository.JobPostingRepo;
import service.sllbackend.service.JobPostingService;
import service.sllbackend.web.dto.JobPostingDTO;
import service.sllbackend.web.dto.JobPostingEditDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobPostingServiceImpl implements JobPostingService {
    private final JobPostingRepo jobPostingRepo;

    @Override
    @Transactional(readOnly = true)
    public List<JobPosting> findJobPosting(String title, String status) {
        title = (title != null && !title.trim().isEmpty()) ? title : null;
        status = (status != null && !status.trim().isEmpty()) ? status : null;

        return jobPostingRepo.searchJobPostings(title, status);
    }

    @Override
    @Transactional
    public void createJobPosting(JobPostingDTO jobPostingDTO) {
        jobPostingRepo.save(JobPosting.builder()
                        .jobPostingName(jobPostingDTO.getJobPostingName())
                        .jobPostingDescription(jobPostingDTO.getJobPostingDescription())
                        .maxApplication(jobPostingDTO.getMaxApplication())
                        .effectiveFrom(jobPostingDTO.getEffectiveFrom())
                        .effectiveTo(jobPostingDTO.getEffectiveTo())
                        .status(jobPostingDTO.getStatus())
                .build());
    }

    @Override
    @Transactional
    public JobPosting editJobPosting(Long id, JobPostingEditDTO jobPostingEditDTO) {
        Optional<JobPosting> optionalJob = jobPostingRepo.findById(id);

        if (optionalJob.isEmpty()) {
            throw new IllegalArgumentException("Job posting not found: " + jobPostingEditDTO.getJobPostingName());
        }

        JobPosting jobPosting = optionalJob.get();

        if (jobPostingEditDTO.getJobPostingName() != null) {
            jobPosting.setJobPostingName(jobPostingEditDTO.getJobPostingName());
        }

        if (jobPostingEditDTO.getJobPostingDescription() != null) {
            jobPosting.setJobPostingDescription(jobPostingEditDTO.getJobPostingDescription());
        }

        if (jobPostingEditDTO.getMaxApplication() != null) {
            jobPosting.setMaxApplication(jobPostingEditDTO.getMaxApplication());
        }

        if (jobPostingEditDTO.getEffectiveFrom() != null) {
            jobPosting.setEffectiveFrom(jobPostingEditDTO.getEffectiveFrom());
        }

        if (jobPostingEditDTO.getEffectiveTo() != null) {
            jobPosting.setEffectiveTo(jobPostingEditDTO.getEffectiveTo());
        }

        if (jobPostingEditDTO.getStatus() != null) {
            jobPosting.setStatus(jobPostingEditDTO.getStatus());
        }

        return jobPostingRepo.save(jobPosting);
    }
}
