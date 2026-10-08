package com.eventbooking.events.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventbooking.events.dto.EventCreateRequestDto;
import com.eventbooking.events.dto.EventResponseDto;
import com.eventbooking.events.dto.EventUpdateRequestDto;

public interface EventService {
    EventResponseDto createEvent(EventCreateRequestDto dto);
    EventResponseDto getEvent(Long id);
    Page<EventResponseDto> getAllEvents(Pageable pageable);
    EventResponseDto updateEvent(Long id ,EventUpdateRequestDto dto);
    EventResponseDto reserveSeats(Long id ,Integer seats);
    void confirmHold(Long id);
    void expireHold(Long holdId);

}
