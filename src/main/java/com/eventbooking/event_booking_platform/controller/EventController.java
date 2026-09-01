package com.eventbooking.event_booking_platform.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event_booking_platform.dto.EventCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.service.EventService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;
    
    public EventController(EventService eventService){
        this.eventService=eventService;
    }


    @PostMapping
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventCreateRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(dto));
    }


    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getEventByid(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEvent(id));
    }

    @GetMapping
public ResponseEntity<Page<EventResponseDto>> getAllEvents(Pageable pageable){
    return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEvents(pageable));
}
    
}
