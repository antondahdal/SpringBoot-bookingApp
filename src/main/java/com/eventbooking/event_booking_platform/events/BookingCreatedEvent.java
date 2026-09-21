package com.eventbooking.event_booking_platform.events;

import lombok.Getter;

@Getter 
public class BookingCreatedEvent {

    public BookingCreatedEvent(Long id ){
        this.id=id;
    }
    private Long id;

}
