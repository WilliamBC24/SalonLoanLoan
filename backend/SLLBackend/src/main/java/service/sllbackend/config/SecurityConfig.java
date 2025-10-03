package service.sllbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import service.sllbackend.service.impl.StaffAccountServiceImpl;
import service.sllbackend.service.impl.UserAccountServiceImpl;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final AuthenticationProvider userAuthenticationProvider;
    private final AuthenticationProvider staffAuthenticationProvider;

    public SecurityConfig(@Qualifier("userAuthenticationProvider") AuthenticationProvider userAuthenticationProvider,
                          @Qualifier("staffAuthenticationProvider") AuthenticationProvider staffAuthenticationProvider) {

        this.userAuthenticationProvider = userAuthenticationProvider;
        this.staffAuthenticationProvider = staffAuthenticationProvider;
    }

    //    @Bean
//    public AuthenticationManager authenticationManager(
//            AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
    @Bean
    public ProviderManager userProviderManager() {
        return new ProviderManager(userAuthenticationProvider);
    }

    @Bean
    public ProviderManager staffProviderManager() {
        return new ProviderManager(staffAuthenticationProvider);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain userSecurityFilter(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                ).authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/user/**", "/css/**", "/js/**", "/api/**").permitAll()
                        .anyRequest().authenticated()
                ).formLogin(formLogin ->
                        formLogin.loginPage("/auth/user/login")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .failureUrl("/auth/user/login?error")
                ).logout(logout ->
                                logout.logoutUrl("/auth/user/logout")
                                        .logoutSuccessUrl("/auth/user/login?logout")
                                        .invalidateHttpSession(true)
                                        .deleteCookies("JSESSIONID")
//        ).authenticationProvider(userAuthenticationProvider()
                ).authenticationManager(userProviderManager()
                ).build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain staffSecurityFilter(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                ).authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/staff/**", "/css/**", "/js/**", "/api/**").permitAll()
                        .anyRequest().authenticated()
                ).formLogin(formLogin ->
                        formLogin.loginPage("/auth/staff/login")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .failureUrl("/auth/staff/login?error")
                ).logout(logout ->
                                logout.logoutUrl("/auth/staff/logout")
                                        .logoutSuccessUrl("/auth/staff/login?logout")
                                        .invalidateHttpSession(true)
                                        .deleteCookies("JSESSIONID")
//        ).authenticationProvider(userAuthenticationProvider()
                ).authenticationManager(staffProviderManager()
                ).build();
    }
}
