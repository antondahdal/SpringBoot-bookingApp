package com.eventbooking.event_booking_platform.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.dto.UserResponseDto;
import com.eventbooking.event_booking_platform.model.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
    private final SecretKey  key;
    private final long expirationMs;

    public JwtServiceImpl(
        @Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-ms}") long expirationMs){
            this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            this.expirationMs = expirationMs;

    }


    public String generateToken(UserResponseDto user) {
        Date now = new Date();
        return Jwts.builder().subject(String.valueOf(user.getId()))
        .claim("email", user.getEmail())
        .claim("role", user.getRole().name()).issuedAt(now).expiration( new Date((now.getTime())+expirationMs))
        .signWith(key).compact();
        
        
    }


    @Override
public JwtPrincipal parseToken(String token) {
    var claims = Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
        .getPayload();

    Long id = Long.parseLong(claims.getSubject());
    String email = claims.get("email", String.class);
    Role role = Role.valueOf(claims.get("role", String.class));

    return new JwtPrincipal(id, email, role);
}


}