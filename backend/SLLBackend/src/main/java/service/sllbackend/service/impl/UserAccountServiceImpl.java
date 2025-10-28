package service.sllbackend.service.impl;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.config.exceptions.BannedException;
import service.sllbackend.config.exceptions.DisabledException;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.enumerator.AccountStatus;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.service.UserAccountService;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
    private final UserAccountRepo userAccountRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        UserAccount user = userAccountRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        
        if (user.getAccountStatus() == AccountStatus.DEACTIVATED) {
            throw new DisabledException(user.getUsername());
        }
        if (user.getAccountStatus() == AccountStatus.BANNED) {
            throw new BannedException(user.getUsername());
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Override
    public List<UserAccount> getSomeByPhoneNumber(String phoneNumber) {
        return userAccountRepo.findTop3ByPhoneNumber(phoneNumber);
    }

    public UserAccount findByUsername(String username) {
        return userAccountRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
