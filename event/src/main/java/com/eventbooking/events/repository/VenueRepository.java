package com.eventbooking.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.events.model.Venue;

public interface VenueRepository  extends JpaRepository<Venue, Long> {
    
}
