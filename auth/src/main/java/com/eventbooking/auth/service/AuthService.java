package com.eventbooking.auth.service;

import com.eventbooking.auth.dto.UserResponseDto;
import com.eventbooking.auth.dto.LoginRequestDto;
import com.eventbooking.auth.dto.RegisterRequestDto;
public interface AuthService {
    public UserResponseDto addNewUser(RegisterRequestDto dto);
    public UserResponseDto authenticate(LoginRequestDto dto);
    public String login(LoginRequestDto dto);
    public UserResponseDto checkIfExist();
}
