package service.sllbackend.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import service.sllbackend.config.exceptions.DisabledException;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.StaffCurrentPosition;
import service.sllbackend.enumerator.AccountStatus;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.repository.StaffCurrentPositionRepo;
import service.sllbackend.service.StaffAccountService;

@Service
@RequiredArgsConstructor
public class StaffAccountServiceImpl implements StaffAccountService {
    private final StaffAccountRepo staffAccountRepo;
    private final StaffCurrentPositionRepo staffCurrentPositionRepo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        StaffAccount staff = staffAccountRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));

        if (staff.getAccountStatus() == AccountStatus.ACTIVE) {
            throw new DisabledException(staff.getUsername());
        }

        List<StaffCurrentPosition> currentPositions = staffCurrentPositionRepo.findAllByStaff(staff.getStaff());
        Set<SimpleGrantedAuthority> authorities = currentPositions.stream().map(pos -> new SimpleGrantedAuthority("ROLE_" + pos.getPosition().getPositionName().toUpperCase().replace(" ", "_"))).collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(staff.getUsername(), staff.getPassword(), authorities);
    }
}