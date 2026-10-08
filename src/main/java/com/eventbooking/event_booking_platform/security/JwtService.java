package com.eventbooking.event_booking_platform.security;

public interface JwtService {
    JwtPrincipal parseToken(String token);
}
