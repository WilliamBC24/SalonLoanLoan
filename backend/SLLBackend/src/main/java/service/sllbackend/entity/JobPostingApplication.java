package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.JobPostingApplicationStatus;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_posting_application")
public class JobPostingApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Column(name = "applicant_name", nullable = false, columnDefinition = "TEXT")
    private String applicantName;

    @Column(name = "applicant_dob", nullable = false)
    private LocalDate applicantDob;

    @Column(name = "applicant_phone_number", nullable = false, length = 20)
    private String applicantPhoneNumber;

    @Column(name = "application_date", nullable = false)
    @Builder.Default
    private LocalDateTime applicationDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "job_posting_application_status_enum")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Builder.Default
    private JobPostingApplicationStatus status = JobPostingApplicationStatus.PENDING;
}
