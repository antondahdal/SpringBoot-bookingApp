package com.eventbooking.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.eventbooking.auth.model.Role;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    
   private Long id;
    private String email;
   private Role role;
}
