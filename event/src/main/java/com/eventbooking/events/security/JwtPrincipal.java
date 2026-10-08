package com.eventbooking.events.security;



import com.eventbooking.events.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtPrincipal {

    private Long userId;
    private String email;
    private Role role;
    
}
