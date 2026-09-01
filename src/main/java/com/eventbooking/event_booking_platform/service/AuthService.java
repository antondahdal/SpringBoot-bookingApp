package com.eventbooking.event_booking_platform.service;

import com.eventbooking.event_booking_platform.dto.UserResponseDto;
import com.eventbooking.event_booking_platform.dto.LoginRequestDto;
import com.eventbooking.event_booking_platform.dto.RegisterRequestDto;
public interface AuthService {
    public UserResponseDto addNewUser(RegisterRequestDto dto);
    public UserResponseDto authenticate(LoginRequestDto dto);
    public String login(LoginRequestDto dto);
}
