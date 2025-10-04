package service.sllbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import service.sllbackend.service.impl.StaffAccountServiceImpl;
import service.sllbackend.service.impl.UserAccountServiceImpl;

@Configuration
@RequiredArgsConstructor
public class AuthenticationProviderConfig {
    private final UserAccountServiceImpl userAccountService;
    private final StaffAccountServiceImpl staffAccountService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public DaoAuthenticationProvider userAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userAccountService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    @Bean
    public DaoAuthenticationProvider staffAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(staffAccountService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
