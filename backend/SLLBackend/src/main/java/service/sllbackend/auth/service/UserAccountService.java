package service.sllbackend.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import service.sllbackend.auth.entity.UserAccount;
import service.sllbackend.auth.entity.enums.AccountRole;
import service.sllbackend.auth.entity.enums.Gender;
import service.sllbackend.auth.repo.UserAccountRepo;

import java.util.List;

@Service
public class UserAccountService implements UserDetailsService {
    private final UserAccountRepo userAccountRepo;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(UserAccountRepo userAccountRepo, PasswordEncoder passwordEncoder) {
        this.userAccountRepo = userAccountRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserAccount user = userAccountRepo.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of() // authorities
        );
    }

    public void registerUser(String username, String rawPassword) {
        String hashedPassword = passwordEncoder.encode(rawPassword);
        userAccountRepo.save(UserAccount.builder()
                .username(username)
                .password(hashedPassword)
                .gender(Gender.male)
                .phoneNumber("0991991991")
                .role(AccountRole.admin)
                .build());
    }
}
