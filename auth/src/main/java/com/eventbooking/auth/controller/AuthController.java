package com.eventbooking.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.auth.dto.LoginRequestDto;
import com.eventbooking.auth.dto.LoginResponseDto;
import com.eventbooking.auth.dto.RegisterRequestDto;
import com.eventbooking.auth.dto.UserResponseDto;
import com.eventbooking.auth.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController  {
    
    private final AuthService  authService;
    public AuthController (AuthService  AuthService){
        this.authService=AuthService;
      
    }

    @PostMapping("/register")
    public ResponseEntity< UserResponseDto> insertUser(@Valid @RequestBody RegisterRequestDto dto){

        return ResponseEntity.status(HttpStatus.CREATED ).body(authService.addNewUser(dto));
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto > login(@Valid @RequestBody LoginRequestDto dto){
        
        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponseDto( authService.login(dto)));
    }


}
