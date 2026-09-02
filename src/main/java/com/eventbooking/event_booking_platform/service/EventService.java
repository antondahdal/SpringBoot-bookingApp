package com.eventbooking.event_booking_platform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventbooking.event_booking_platform.dto.EventCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.dto.EventUpdateRequestDto;

public interface EventService {
    EventResponseDto createEvent(EventCreateRequestDto dto);
    EventResponseDto getEvent(Long id);
    Page<EventResponseDto> getAllEvents(Pageable pageable);
    EventResponseDto updateEvent(Long id ,EventUpdateRequestDto dto);

}
