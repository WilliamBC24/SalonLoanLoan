package service.sllbackend.service;

import service.sllbackend.web.dto.JobApplicationDTO;

public interface JobApplicationService {
    void saveApplication(Long jobId, JobApplicationDTO jobApplicationDTO);
    void acceptApplication(Long applicationId);
    void rejectApplication(Long applicationId);
    boolean hasAlreadyApplied(Long jobId, String phoneNumber);
}
