package service.sllbackend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import service.sllbackend.entity.StaffAccount;

import java.time.LocalDateTime;
import java.util.List;

public interface StaffAccountService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);
    List<StaffAccount> findAllActive();
    List<StaffAccount> findActiveStaffInShiftByScheduledAt(LocalDateTime scheduledAt);
}
