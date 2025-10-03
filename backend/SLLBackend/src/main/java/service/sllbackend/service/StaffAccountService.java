package service.sllbackend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface StaffAccountService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
}
