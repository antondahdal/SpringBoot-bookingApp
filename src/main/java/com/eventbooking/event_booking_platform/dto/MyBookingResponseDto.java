package com.eventbooking.event_booking_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyBookingResponseDto {
    private Long id;
    private String eventTitle;
    private int seats;
}
