package com.eventbooking.event_booking_platform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/events/{eventId}/bookings")
public class BookingController {
    private final BookingService bookingService;
    public BookingController (BookingService bookingService){
        this.bookingService=bookingService;
    }

    @PostMapping
    public ResponseEntity< BookingResponseDto> book(@Valid @RequestBody BookingCreateRequestDto dto,@PathVariable long eventId){


        return  ResponseEntity.status(HttpStatus.CREATED).body(bookingService.book(dto, eventId));
    }
    
}
