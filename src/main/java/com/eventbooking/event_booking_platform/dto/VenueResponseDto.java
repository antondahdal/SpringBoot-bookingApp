package com.eventbooking.event_booking_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VenueResponseDto {
    private Long id;
    private String name;
    private String address;
    private int capacity;
}