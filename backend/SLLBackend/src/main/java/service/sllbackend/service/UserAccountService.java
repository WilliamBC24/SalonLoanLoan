package service.sllbackend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import service.sllbackend.entity.UserAccount;

import java.util.List;

public interface UserAccountService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
    List<UserAccount> getSomeByPhoneNumber(String phoneNumber);
    UserAccount findByUsername(String username);
}