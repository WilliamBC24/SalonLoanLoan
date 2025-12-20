package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.JobPosting;
import service.sllbackend.entity.JobPostingApplication;
import service.sllbackend.enumerator.JobPostingApplicationStatus;
import service.sllbackend.enumerator.JobPostingStatus;
import service.sllbackend.repository.JobPostingApplicationRepo;
import service.sllbackend.repository.JobPostingRepo;
import service.sllbackend.service.JobApplicationService;
import service.sllbackend.web.dto.JobApplicationDTO;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {
    private final JobPostingRepo jobPostingRepo;
    private final JobPostingApplicationRepo jobPostingApplicationRepo;

    @Override
    @Transactional
    public void saveApplication(Long jobId, JobApplicationDTO jobApplicationDTO) {
        JobPosting job = jobPostingRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found"));
        
        // Check if applicant has already applied
        if (jobPostingApplicationRepo.existsByJobPostingAndApplicantPhoneNumber(job, jobApplicationDTO.getApplicantPhoneNumber())) {
            throw new IllegalArgumentException("You have already applied for this job posting");
        }
        
        jobPostingApplicationRepo.save(JobPostingApplication.builder()
                .jobPosting(job)
                .applicantName(jobApplicationDTO.getApplicantName())
                .applicantDob(jobApplicationDTO.getApplicantDob())
                .applicantPhoneNumber(jobApplicationDTO.getApplicantPhoneNumber())
                .build());
        long currentApplications = jobPostingApplicationRepo.countByJobPostingAndStatusNot(job, JobPostingApplicationStatus.REJECTED);
        if (currentApplications == job.getMaxApplication()) {
            job.setStatus(JobPostingStatus.DEACTIVATED);
            jobPostingRepo.save(job);
        }
    }

    @Override
    @Transactional
    public void acceptApplication(Long applicationId) {
        JobPostingApplication jobPostingApplication = jobPostingApplicationRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        jobPostingApplication.setStatus(JobPostingApplicationStatus.ACCEPTED);
        jobPostingApplicationRepo.save(jobPostingApplication);
    }

    @Override
    @Transactional
    public void rejectApplication(Long applicationId) {
        JobPostingApplication jobPostingApplication = jobPostingApplicationRepo.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        jobPostingApplication.setStatus(JobPostingApplicationStatus.REJECTED);
        jobPostingApplicationRepo.save(jobPostingApplication);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAlreadyApplied(Long jobId, String phoneNumber) {
        JobPosting job = jobPostingRepo.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found"));
        return jobPostingApplicationRepo.existsByJobPostingAndApplicantPhoneNumber(job, phoneNumber);
    }


}
