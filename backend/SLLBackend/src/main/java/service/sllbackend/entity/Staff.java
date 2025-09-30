package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.StaffStatus;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_hired", nullable = false)
    @Builder.Default
    private LocalDate dateHired = LocalDate.now();

    @Column(name = "end_of_contract_date")
    private LocalDate endOfContractDate;

    @Column(name = "social_security_num", length = 60)
    private String socialSecurityNum;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(columnDefinition = "TEXT")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_status", nullable = false, columnDefinition = "staff_status_enum")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Builder.Default
    private StaffStatus staffStatus = StaffStatus.ACTIVE;
}
