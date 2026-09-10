package com.eventbooking.event_booking_platform;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.dto.LoginRequestDto;
import com.eventbooking.event_booking_platform.dto.LoginResponseDto;
import com.eventbooking.event_booking_platform.dto.RegisterRequestDto;
import com.eventbooking.event_booking_platform.model.Event;
import com.eventbooking.event_booking_platform.model.Venue;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.VenueRepository;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
            "server.port=8181",
            "event.service.base-url=http://localhost:8181"
        })
@AutoConfigureTestRestTemplate
public class BookingEventCallIT {
    
    @Autowired 
    private  TestRestTemplate template ;
    @Autowired 
    private VenueRepository venueRepository;
    @Autowired 
    private EventRepository  eventRepository;

    @Test
    void bookCallsEventOverHttp() {
        Venue venue = new Venue();
        venue.setName("Arena");
        venue.setAddress("1 Main St");
        venue.setCapacity(10);
        venue = venueRepository.save(venue);

        Event event = new Event();
        event.setVenue(venue);
        event.setTitle("Concert");
        event.setDateTime(LocalDateTime.of(2026, 9, 10, 20, 0));
        event.setTotalSeats(2);
        event.setAvailableSeats(2);
        event = eventRepository.save(event);

        RegisterRequestDto register = new RegisterRequestDto();
        register.setEmail("anton-it@test.com");
        register.setPassword("secret");
        template.postForEntity("/api/auth/register", register, Void.class);

        LoginRequestDto login = new LoginRequestDto();
        login.setEmail("anton-it@test.com");
        login.setPassword("secret");
        ResponseEntity<LoginResponseDto> loginRes =
            template.postForEntity("/api/auth/login", login, LoginResponseDto.class);
        String token = loginRes.getBody().getToken();

        BookingCreateRequestDto bookBody = new BookingCreateRequestDto();
        bookBody.setSeats(1);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingCreateRequestDto> bookReq = new HttpEntity<>(bookBody, headers);
        ResponseEntity<BookingResponseDto> bookRes = template.postForEntity(
            "/api/events/" + event.getId() + "/bookings",
            bookReq,
            BookingResponseDto.class);

        assertThat(bookRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<EventResponseDto> eventRes =
            template.getForEntity("/api/events/" + event.getId(), EventResponseDto.class);
        assertThat(eventRes.getBody().getAvailableSeats()).isEqualTo(1);
    }
}
