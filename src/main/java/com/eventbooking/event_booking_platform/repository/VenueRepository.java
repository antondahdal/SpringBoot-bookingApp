package com.eventbooking.event_booking_platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.event_booking_platform.model.Venue;

public interface VenueRepository  extends JpaRepository<Venue, Long> {
    
}
