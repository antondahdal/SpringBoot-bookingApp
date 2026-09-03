package com.eventbooking.event_booking_platform.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.exception.InsufficientSeatsException;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Booking;
import com.eventbooking.event_booking_platform.model.Event;
import com.eventbooking.event_booking_platform.model.User;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.UserRepository;
import com.eventbooking.event_booking_platform.client.EventClient;
import jakarta.transaction.Transactional;
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final  UserRepository userRepository;
    private final   EventRepository eventRepository;
    private final   EventClient eventClient;

    public BookingServiceImpl( BookingRepository bookingRepository,
        UserRepository userRepository,
        EventRepository eventRepository,EventClient eventClient){
            this.bookingRepository=bookingRepository;
            this.userRepository=userRepository;
            this.eventRepository=eventRepository;
            this.eventClient=eventClient;

    }
    
    @Transactional
    public BookingResponseDto book (BookingCreateRequestDto dto,long id){
        User user =userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new ResourceNotFoundException("There is no Such User"));
        Booking bookToSave= new Booking();
       eventClient.reserveSeats(id, dto.getSeats());
        Event eventToBook=eventRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(" Event not found "));
        bookToSave.setEvent(eventToBook);
        bookToSave.setSeats(dto.getSeats());
        bookToSave.setUser(user);
       Booking book=bookingRepository.save(bookToSave);
       
        return new BookingResponseDto(book.getId(),book.getEvent().getId(), book.getUser().getId(),book.getSeats());
    }

   

}
