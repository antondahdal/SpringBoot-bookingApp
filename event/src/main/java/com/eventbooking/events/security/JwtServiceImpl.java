package com.eventbooking.events.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eventbooking.events.model.Role;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {

	private final SecretKey key;

	public JwtServiceImpl(@Value("${app.jwt.secret}") String secret) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
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
