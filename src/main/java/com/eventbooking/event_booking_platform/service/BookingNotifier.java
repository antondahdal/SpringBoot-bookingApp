package com.eventbooking.event_booking_platform.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service 
public class BookingNotifier {
    public void send(Long bookingId) {
        System.out.println("ticket " + bookingId);
    }
}
