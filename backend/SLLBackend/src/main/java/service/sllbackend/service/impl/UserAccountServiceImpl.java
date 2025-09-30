package service.sllbackend.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.service.UserAccountService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepo userAccountRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserAccount user = userAccountRepo.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of() // authorities
        );
    }
}