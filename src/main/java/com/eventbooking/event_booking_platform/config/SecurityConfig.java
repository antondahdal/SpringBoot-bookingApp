package com.eventbooking.event_booking_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.eventbooking.event_booking_platform.security.JwtAuthenticationFilter;
import com.eventbooking.event_booking_platform.security.JwtService;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    public SecurityConfig(JwtService jwtService){
        this.jwtService=jwtService;
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
                .requestMatchers(HttpMethod.GET, "/api/events/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/events").hasAnyRole("ORGANIZER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/venues").hasAnyRole("ORGANIZER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/events/*/bookings").hasRole("ATTENDEE")
                .requestMatchers("/error").permitAll() 
                .anyRequest().authenticated())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));

            http.addFilterBefore(
                new JwtAuthenticationFilter(jwtService),
                UsernamePasswordAuthenticationFilter.class
            );
          
        return http.build();
    }
}
