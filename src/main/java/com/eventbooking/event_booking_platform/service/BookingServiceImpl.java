package com.eventbooking.event_booking_platform.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.dto.UserResponseDto;
import com.eventbooking.event_booking_platform.exception.InsufficientSeatsException;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Booking;
import com.eventbooking.event_booking_platform.model.Event;
import com.eventbooking.event_booking_platform.model.User;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.UserRepository;
import com.eventbooking.event_booking_platform.client.AuthClient;
import com.eventbooking.event_booking_platform.client.EventClient;
import jakarta.transaction.Transactional;
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final  UserRepository userRepository;
    private final   EventRepository eventRepository;
    private final   EventClient eventClient;
    private final AuthClient authClient;

    public BookingServiceImpl( BookingRepository bookingRepository,
        UserRepository userRepository,
        EventRepository eventRepository,EventClient eventClient,AuthClient authClient){
            this.bookingRepository=bookingRepository;
            this.userRepository=userRepository;
            this.eventRepository=eventRepository;
            this.eventClient=eventClient;
            this.authClient=authClient;

    } 
    
    @Transactional
    public BookingResponseDto book (BookingCreateRequestDto dto,long id){
        UserResponseDto resUser= authClient.checkIfExist();
         User user =userRepository.findById(resUser.getId()).orElseThrow(()->new ResourceNotFoundException("There is no Such User"));
        Booking bookToSave= new Booking();
       eventClient.reserveSeats(id, dto.getSeats());
        Event eventToBook=eventRepository.getReferenceById(id);
        bookToSave.setEvent(eventToBook);
        bookToSave.setSeats(dto.getSeats());
        bookToSave.setUser(user);
       Booking book=bookingRepository.save(bookToSave);
       
        return new BookingResponseDto(book.getId(),book.getEvent().getId(), book.getUser().getId(),book.getSeats());
    }


   

}
