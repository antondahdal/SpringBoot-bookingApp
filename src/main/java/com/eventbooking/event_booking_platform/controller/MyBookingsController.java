package com.eventbooking.event_booking_platform.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event_booking_platform.dto.MyBookingResponseDto;
import com.eventbooking.event_booking_platform.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class MyBookingsController {
    private final BookingService bookingService;

    public MyBookingsController(BookingService bookingService){
        this.bookingService=bookingService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<MyBookingResponseDto>> myBookings(){
        return ResponseEntity.ok(bookingService.myBookings());
    }
}
