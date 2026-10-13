package com.eventbooking.events.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import static org.assertj.core.api.Assertions.assertThat;

import com.eventbooking.events.model.Event;
import com.eventbooking.events.model.Venue;

import jakarta.persistence.EntityManagerFactory;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
class EventListQueryCountTest {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    VenueRepository venueRepository;
    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Test
    void pageOfEventsLoadsVenueInOneQuery() {
        for (int i = 0; i < 5; i++) {
            Venue venue = new Venue();
            venue.setName("Hall " + i);
            venue.setAddress("Street " + i);
            venue.setCapacity(100);
            venue = venueRepository.save(venue);

            Event event = new Event();
            event.setVenue(venue);
            event.setTitle("Show " + i);
            event.setDateTime(LocalDateTime.of(2026, 10, 13, 20, 0));
            event.setTotalSeats(50);
            event.setAvailableSeats(50);
            eventRepository.save(event);
        }
        eventRepository.flush();

        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        Page<Long> ids = eventRepository.findIds(PageRequest.of(0, 2));
        List<Event> events = eventRepository.findWithVenueByIdIn(ids.getContent());

        assertThat(ids.getTotalElements()).isEqualTo(5);
        assertThat(events).hasSize(2);
        assertThat(events).allMatch(event -> event.getVenue().getName() != null);
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(3);
    }
}
