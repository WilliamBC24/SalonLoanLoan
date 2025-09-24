package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.enums.StaffPositionEnum;
import service.sllbackend.auth.entity.enums.StaffStatusEnum;

import java.time.LocalDate;

@Entity
@Table(name = "staff")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccount user;

    @Column(nullable = false)
    private Double salary;

    @Column(name = "date_hired", nullable = false)
    private LocalDate dateHired;

    @Column(name = "end_of_contract_date")
    private LocalDate endOfContractDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffPositionEnum position = StaffPositionEnum.STAFF;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_status", nullable = false)
    private StaffStatusEnum staffStatus = StaffStatusEnum.ACTIVE;
}
