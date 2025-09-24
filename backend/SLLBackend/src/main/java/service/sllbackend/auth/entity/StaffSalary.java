package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.enums.AccountRoleEnum;

@Entity
@Table(name = "staff_salary")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StaffSalary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private AccountRoleEnum role;

    @Column(nullable = false)
    private Double base;
}
