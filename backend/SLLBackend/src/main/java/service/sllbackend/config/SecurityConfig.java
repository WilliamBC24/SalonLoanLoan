package service.sllbackend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
	private final AuthenticationProvider userAuthenticationProvider;
	private final AuthenticationProvider staffAuthenticationProvider;

	public SecurityConfig(
			@Qualifier("userAuthenticationProvider") AuthenticationProvider userAuthenticationProvider,
			@Qualifier("staffAuthenticationProvider") AuthenticationProvider staffAuthenticationProvider) {

		this.userAuthenticationProvider = userAuthenticationProvider;
		this.staffAuthenticationProvider = staffAuthenticationProvider;
	}

	@Bean
	@Primary
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
				.securityMatcher("/auth/user/**")
				.csrf(csrf -> csrf
						.ignoringRequestMatchers("/api/**"))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/user/**", "/css/**", "/js/**", "/api/**")
						.permitAll()
						.anyRequest().authenticated())
				.formLogin(formLogin -> formLogin.loginPage("/auth/user/login")
						.usernameParameter("username")
						.passwordParameter("password")
						.failureUrl("/auth/user/login?error"))
				.authenticationManager(userProviderManager())
				.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain staffSecurityFilter(HttpSecurity http) throws Exception {
		return http
				.securityMatcher("/auth/staff/**")
				.csrf(csrf -> csrf
						.ignoringRequestMatchers("/api/**"))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/staff/**", "/css/**", "/js/**", "/api/**")
						.permitAll()
						.anyRequest().authenticated())
				.formLogin(formLogin -> formLogin.loginPage("/auth/staff/login")
						.usernameParameter("username")
						.passwordParameter("password")
						.failureUrl("/auth/staff/login?error"))
				.authenticationManager(staffProviderManager())
				.build();
	}

	@Bean
	@Order(3)
	public SecurityFilterChain logoutSecurityFilter(HttpSecurity http) throws Exception {
		return http
				.securityMatcher("/auth/logout")
				.csrf(csrf -> csrf
						.ignoringRequestMatchers("/api/**"))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/auth/logout").permitAll()
						.anyRequest().authenticated())
				.logout(logout -> logout.logoutUrl("/auth/logout")
						.logoutSuccessUrl("/?logout")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID"))
				.build();
	}

	@Bean
	@Order(4)
	public SecurityFilterChain defaultSecurityFilter(HttpSecurity http) throws Exception {
		return http
				.securityMatcher("/**")
				.csrf(csrf -> csrf
						.ignoringRequestMatchers("/api/**"))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/css/**", "/js/**", "/api/**").permitAll()
						.anyRequest().authenticated())
				.formLogin(formLogin -> formLogin.loginPage("/auth/user/login")
						.usernameParameter("username")
						.passwordParameter("password")
						.failureUrl("/auth/user/login?error"))
				.authenticationManager(userProviderManager())
				.build();
	}
}
