package com.eventbooking.event_booking_platform.security;

import com.eventbooking.event_booking_platform.dto.UserResponseDto;

public interface JwtService {
    public String generateToken(UserResponseDto user);
    public JwtPrincipal  parseToken(String token);
}
