package com.eventbooking.event_booking_platform.service;

import java.util.List;

import com.eventbooking.event_booking_platform.dto.VenueCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.VenueResponseDto;

public interface VenueService {
    public VenueResponseDto createVenue(VenueCreateRequestDto dto);
    public VenueResponseDto getVenue(Long id);
    public List<VenueResponseDto> getAllVenues();



}
