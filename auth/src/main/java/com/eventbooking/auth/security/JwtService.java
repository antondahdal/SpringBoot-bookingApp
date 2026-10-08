package com.eventbooking.auth.security;

import com.eventbooking.auth.dto.UserResponseDto;

public interface JwtService {
    public String generateToken(UserResponseDto user);
    public JwtPrincipal  parseToken(String token);
}
