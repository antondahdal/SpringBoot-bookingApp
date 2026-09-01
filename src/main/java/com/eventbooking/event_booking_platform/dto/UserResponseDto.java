package com.eventbooking.event_booking_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.eventbooking.event_booking_platform.model.Role;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    
   private Long id;
    private String email;
   private Role role;
}
