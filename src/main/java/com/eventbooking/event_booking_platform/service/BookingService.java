package com.eventbooking.event_booking_platform.service;

import java.util.List;

import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.dto.MyBookingResponseDto;

public interface BookingService {
    public BookingResponseDto book (BookingCreateRequestDto dto,long id);
    public List<MyBookingResponseDto> myBookings();
}
