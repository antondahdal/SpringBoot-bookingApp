package com.eventbooking.events.service;

import java.util.List;

import com.eventbooking.events.dto.VenueCreateRequestDto;
import com.eventbooking.events.dto.VenueResponseDto;

public interface VenueService {
    public VenueResponseDto createVenue(VenueCreateRequestDto dto);
    public VenueResponseDto getVenue(Long id);
    public List<VenueResponseDto> getAllVenues();



}
