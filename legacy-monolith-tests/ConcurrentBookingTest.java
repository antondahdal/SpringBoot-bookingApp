package com.eventbooking.event_booking_platform;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.model.Event;
import com.eventbooking.event_booking_platform.model.Role;
import com.eventbooking.event_booking_platform.model.User;
import com.eventbooking.event_booking_platform.model.Venue;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.UserRepository;
import com.eventbooking.event_booking_platform.repository.VenueRepository;
import com.eventbooking.event_booking_platform.service.BookingService;

@SpringBootTest
public class ConcurrentBookingTest {
    @Autowired
    BookingService bookingService;
    @Autowired
    VenueRepository VenueRepository;
    @Autowired
    EventRepository EventRepository;
    @Autowired
    UserRepository UserRepository;
    @Autowired
    BookingRepository BookingRepository;

    @Test
    void concTest() throws InterruptedException {
        Venue venue = new Venue();
        venue.setName("Arena");
        venue.setAddress("1 Main St");
        venue.setCapacity(1);
        venue = VenueRepository.save(venue);

        Event event = new Event();
        event.setVenue(venue);
        event.setTitle("Concert");
        event.setDateTime(LocalDateTime.of(2026, 9, 2, 20, 0));
        event.setTotalSeats(1);
        event.setAvailableSeats(1);
        event = EventRepository.save(event);

        User user = new User();
        user.setEmail("anton@test.com");
        user.setPasswordHash("not-used");
        user.setRole(Role.ATTENDEE);
        UserRepository.save(user);

        BookingCreateRequestDto dto = new BookingCreateRequestDto();
        dto.setSeats(1);

        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        CopyOnWriteArrayList<Throwable> failures = new CopyOnWriteArrayList<>();
        Long eventId = event.getId();

        Runnable bookOnce = () -> {
            try {
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("anton@test.com", "n/a"));
                start.await();
                bookingService.book(dto, eventId);
                successes.incrementAndGet();
            } catch (Exception e) {
                failures.add(e);
            }
        };

        Thread t1 = new Thread(bookOnce);
        Thread t2 = new Thread(bookOnce);
        t1.start();
        t2.start();
        start.countDown();
        t1.join();
        t2.join();

        Event after = EventRepository.findById(eventId).orElseThrow();
        assertThat(after.getAvailableSeats()).isEqualTo(0);
        assertThat(BookingRepository.count()).isEqualTo(1);
        assertThat(successes.get()).isEqualTo(1);
        assertThat(failures).hasSize(1);
    }
}