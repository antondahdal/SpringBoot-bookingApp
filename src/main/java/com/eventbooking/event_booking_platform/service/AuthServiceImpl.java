package com.eventbooking.event_booking_platform.service;

import com.eventbooking.event_booking_platform.dto.LoginRequestDto;
import com.eventbooking.event_booking_platform.dto.RegisterRequestDto;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.dto.UserResponseDto;
import com.eventbooking.event_booking_platform.exception.DuplicateException;
import com.eventbooking.event_booking_platform.exception.InvalidCredentialsException;
import com.eventbooking.event_booking_platform.model.Role;
import com.eventbooking.event_booking_platform.model.User;
import com.eventbooking.event_booking_platform.repository.UserRepository;
import com.eventbooking.event_booking_platform.security.JwtService;
@Service
public class AuthServiceImpl  implements AuthService {
    private final  UserRepository userRepo;
    private final PasswordEncoder passEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepo,PasswordEncoder passEncoder,JwtService jwtService){
        this.userRepo=userRepo;
        this.passEncoder=passEncoder;
        this.jwtService=jwtService;
    }
    
    @Override
    public UserResponseDto addNewUser(RegisterRequestDto dto) {
        if(userRepo.existsByEmail(dto.getEmail())){
            throw new DuplicateException("duplicate User ");
        }
        User newUser=new User();
        newUser.setEmail(dto.getEmail());
        newUser.setPasswordHash(passEncoder.encode(dto.getPassword()));
        newUser.setRole(Role.ATTENDEE);
        User retrunedUser= userRepo.save(newUser);

        return  new UserResponseDto(retrunedUser.getId(),retrunedUser.getEmail(),retrunedUser.getRole());

       
    }

    @Override
    public UserResponseDto authenticate(LoginRequestDto dto) {
       User returnedUser=userRepo.findByEmail(dto.getEmail()).orElseThrow(()->new InvalidCredentialsException("Invalid Credintals"));
       if( !passEncoder.matches(dto.getPassword(), returnedUser.getPasswordHash())) throw new InvalidCredentialsException("Invalid Credintals");
       return  new UserResponseDto(returnedUser.getId(),returnedUser.getEmail(),returnedUser.getRole());

    }

    @Override
    public String login(LoginRequestDto dto) {
       return jwtService.generateToken(authenticate(dto));
       
    }
    
    

    
    
}
