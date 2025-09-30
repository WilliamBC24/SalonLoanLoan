package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.JobPostingStatus;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_posting")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "job_posting_name", nullable = false, columnDefinition = "TEXT")
    private String jobPostingName;

    @Column(name = "job_posting_description", nullable = false, columnDefinition = "TEXT")
    private String jobPostingDescription;

    @Column(name = "max_application", nullable = false)
    private Integer maxApplication;

    @Column(name = "effective_from", nullable = false)
    @Builder.Default
    private LocalDate effectiveFrom = LocalDate.now();

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "job_posting_status_enum")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Builder.Default
    private JobPostingStatus status = JobPostingStatus.DEACTIVATED;
}
