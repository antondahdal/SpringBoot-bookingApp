package com.eventbooking.auth.security;



import com.eventbooking.auth.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtPrincipal {

    private Long userId;
    private String email;
    private Role role;
    
}
