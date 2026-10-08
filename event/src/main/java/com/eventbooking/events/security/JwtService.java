package com.eventbooking.events.security;

public interface JwtService {
	JwtPrincipal parseToken(String token);
}
