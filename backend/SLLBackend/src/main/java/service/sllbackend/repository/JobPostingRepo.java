package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.JobPosting;
import service.sllbackend.enumerator.JobPostingStatus;

import java.util.List;
import java.util.Optional;

public interface JobPostingRepo extends JpaRepository<JobPosting, Long> {
    Optional<JobPosting> findByJobPostingName(String name);
    List<JobPosting> findAllByStatus(JobPostingStatus status);
    @Query(value = """
    SELECT * FROM job_posting jp
    WHERE (:title IS NULL OR LOWER(jp.job_posting_name) LIKE LOWER(CONCAT('%', :title, '%')))
      AND (:status IS NULL OR jp.status = :status)
    """,
            nativeQuery = true)
    List<JobPosting> searchJobPostings(@Param("title") String title, @Param("status") String status);
}