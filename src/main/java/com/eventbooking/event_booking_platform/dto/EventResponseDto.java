package com.eventbooking.event_booking_platform.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class EventResponseDto {
    private  Long id;
    private Long venueId;
   private String venueName;
    private  String title;
    private  LocalDateTime dateTime;
    private  int totalSeats;
    private  int availableSeats;
}
