package com.eventbooking.event_booking_platform.repository;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.eventbooking.event_booking_platform.model.Event;
import com.eventbooking.event_booking_platform.model.Venue;

@DataJpaTest
class EventRepositoryTest {
    @Autowired
    EventRepository eventRepository;
    @Autowired
    VenueRepository venueRepository;
    // next: save entity, findById, assert

    @Test
void saveAndFindById() {
    Venue venue = new Venue();
   venue.setAddress("xyz");
   venue.setCapacity(5);
   venue.setName("Zbe");
    venue = venueRepository.save(venue);

    Event event = new Event();
    event.setVenue(venue);
    event.setTitle("Concert");
    event.setDateTime(LocalDateTime.of(2026, 9, 1, 20, 0));
    event.setTotalSeats(100);
    event.setAvailableSeats(100);
    event = eventRepository.save(event);

    Event found = eventRepository.findById(event.getId()).orElseThrow();
    assertThat(found.getTitle()).isEqualTo("Concert");
}
}