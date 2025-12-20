package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.JobPosting;
import service.sllbackend.entity.JobPostingApplication;
import service.sllbackend.enumerator.JobPostingApplicationStatus;

public interface JobPostingApplicationRepo extends JpaRepository<JobPostingApplication, Long> {
    long countByJobPostingAndStatusNot(JobPosting job, JobPostingApplicationStatus status);
    boolean existsByJobPostingAndApplicantPhoneNumber(JobPosting jobPosting, String applicantPhoneNumber);
}
