package com.eventbooking.event_booking_platform.security;



import com.eventbooking.event_booking_platform.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtPrincipal {

    private Long userId;
    private String email;
    private Role role;
    
}
