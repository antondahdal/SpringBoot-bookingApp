package com.eventbooking.event_booking_platform.client;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import com.eventbooking.event_booking_platform.dto.UserResponseDto;

import io.github.resilience4j.retry.annotation.Retry;

@Component 
public class AuthClient {
    private final WebClient webClient;

    public  AuthClient(WebClient webClient){

        this.webClient=webClient;
    }
    @Retry(name = "eventGet") 
    public UserResponseDto checkIfExist(){
       
        ServletRequestAttributes attrs =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String authHeader = attrs.getRequest().getHeader("Authorization");
        String correlationId = (String) attrs.getRequest().getAttribute("X-Correlation-Id");

        return webClient.get().uri("/api/users/me")
        .header("Authorization", authHeader)
        .header("X-Correlation-Id", correlationId)
        .retrieve()
        .bodyToMono(UserResponseDto.class).block();
    }
}
