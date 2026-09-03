package com.eventbooking.event_booking_platform.client;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.dto.SeatReservationRequestDto;

@Component
public class EventClient {
    
    private final WebClient webClient;
    public EventClient(WebClient webClient){
        this.webClient=webClient;
    }


    public EventResponseDto reserveSeats(Long eventId, int seats){
        SeatReservationRequestDto dto = new SeatReservationRequestDto();
        dto.setSeats(seats);
        ServletRequestAttributes attrs =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String authHeader = attrs.getRequest().getHeader("Authorization");
      return  webClient.post().uri("/api/events/{id}/seat-reservations", eventId).header("Authorization", authHeader).
      bodyValue(dto ).retrieve().bodyToMono(EventResponseDto.class).block();
    }
}
