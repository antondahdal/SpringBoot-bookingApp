package com.eventbooking.event_booking_platform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event_booking_platform.dto.UserResponseDto;
import com.eventbooking.event_booking_platform.service.AuthService;



@RestController
@RequestMapping("/api/users")
public class UserController {

   private final AuthService  authService;
   public UserController (AuthService  authService){
   this.authService =authService;
   }

    @GetMapping ("/me")
     public ResponseEntity<UserResponseDto> getUser( ){
        


        return ResponseEntity.status(HttpStatus.OK).body( authService.checkIfExist());
     }

}
