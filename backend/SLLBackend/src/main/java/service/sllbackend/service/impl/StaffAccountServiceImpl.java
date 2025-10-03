package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import service.sllbackend.config.exceptions.DisabledException;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.StaffCurrentPosition;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.repository.StaffCurrentPositionRepo;
import service.sllbackend.service.StaffAccountService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffAccountServiceImpl implements StaffAccountService {
    private final StaffAccountRepo staffAccountRepo;
    private final StaffCurrentPositionRepo staffCurrentPositionRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        StaffAccount staff = staffAccountRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        if (staff.getActiveStatus() == Boolean.FALSE) {
            throw new DisabledException(staff.getUsername());
        }
        List<StaffCurrentPosition> currentPositions = staffCurrentPositionRepo.findAllByStaff(staff.getStaff());
        Set<SimpleGrantedAuthority> authorities = currentPositions.stream()
                .map(pos -> new SimpleGrantedAuthority("ROLE_" + pos.getPosition().getPositionName().toUpperCase().replace(" ", "_")))
                .collect(Collectors.toSet());

        return new org.springframework.security.core.userdetails.User(
                staff.getUsername(),
                staff.getPassword(),
                authorities
        );
    }
}
