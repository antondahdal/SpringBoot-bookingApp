package com.eventbooking.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eventbooking.auth.security.JwtAuthenticationFilter;
import com.eventbooking.auth.security.JwtService;

@Configuration
public class SecurityConfig {

	private final JwtService jwtService;

	public SecurityConfig(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/actuator/health/**").permitAll()
				.anyRequest().authenticated())
			.formLogin(form -> form.disable())
			.httpBasic(basic -> basic.disable())
			.headers(headers -> headers.frameOptions(frame -> frame.disable()));

		http.addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(new CorrelationIdFilter(), JwtAuthenticationFilter.class);
		return http.build();
	}
}
