package com.eventbooking.events.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.eventbooking.events.security.JwtAuthenticationFilter;
import com.eventbooking.events.security.JwtService;

@Configuration
public class SecurityConfig {

	private final JwtService jwtService;
	private final String internalToken;

	public SecurityConfig(JwtService jwtService, @Value("${app.internal.token}") String internalToken) {
		this.jwtService = jwtService;
		this.internalToken = internalToken;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/venues/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/events").hasAnyRole("ORGANIZER", "ADMIN")
				.requestMatchers(HttpMethod.PATCH, "/api/events/**").hasAnyRole("ORGANIZER", "ADMIN")
				.requestMatchers(HttpMethod.POST, "/api/venues").hasAnyRole("ORGANIZER", "ADMIN")
				.requestMatchers(HttpMethod.POST, "/api/events/*/seat-reservations").authenticated()
				.requestMatchers(HttpMethod.POST, "/api/events/holds/*/confirm").hasRole("INTERNAL")
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/actuator/health/**").permitAll()
				.anyRequest().authenticated())
			.formLogin(form -> form.disable())
			.httpBasic(basic -> basic.disable())
			.headers(headers -> headers.frameOptions(frame -> frame.disable()));

			http.addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
			http.addFilterBefore(new InternalTokenFilter(internalToken), JwtAuthenticationFilter.class);
			http.addFilterBefore(new CorrelationIdFilter(), JwtAuthenticationFilter.class);
			
		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:3000"));
		config.setAllowedMethods(List.of("GET", "POST", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Correlation-Id"));
		config.setExposedHeaders(List.of("X-Correlation-Id"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", config);
		return source;
	}
}
