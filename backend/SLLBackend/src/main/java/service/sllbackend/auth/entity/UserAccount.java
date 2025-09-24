package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.enums.AccountRoleEnum;
import service.sllbackend.auth.entity.enums.AccountStatusEnum;
import service.sllbackend.auth.entity.enums.GenderEnum;

@Entity
@Table(name = "user_account")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderEnum gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountRoleEnum role = AccountRoleEnum.CUSTOMER;

    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatusEnum accountStatus = AccountStatusEnum.ACTIVE;
}
