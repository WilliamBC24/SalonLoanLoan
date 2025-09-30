package service.sllbackend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AccountRole;
import service.sllbackend.enumerator.Gender;
import service.sllbackend.repository.UserAccountRepo;

import java.util.List;

// TODO: Solidify

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
                .gender(Gender.MALE)
                .phoneNumber("0991991991")
                .role(AccountRole.ADMIN)
                .build());
    }
}
