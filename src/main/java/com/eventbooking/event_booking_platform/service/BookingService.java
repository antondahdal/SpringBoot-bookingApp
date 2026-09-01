package com.eventbooking.event_booking_platform.service;

import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;

public interface BookingService {
    public BookingResponseDto book (BookingCreateRequestDto dto,long id);
}
