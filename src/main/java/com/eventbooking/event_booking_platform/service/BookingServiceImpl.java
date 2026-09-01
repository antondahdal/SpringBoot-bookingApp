package com.eventbooking.event_booking_platform.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.exception.InsufficientSeatsException;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Booking;
import com.eventbooking.event_booking_platform.model.Event;
import com.eventbooking.event_booking_platform.model.User;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.UserRepository;

import jakarta.transaction.Transactional;
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final  UserRepository userRepository;
    private final   EventRepository eventRepository;

    public BookingServiceImpl( BookingRepository bookingRepository,
        UserRepository userRepository,
        EventRepository eventRepository){
            this.bookingRepository=bookingRepository;
            this.userRepository=userRepository;
            this.eventRepository=eventRepository;

    }
    
    @Transactional
    public BookingResponseDto book (BookingCreateRequestDto dto,long id){
        User user =userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new ResourceNotFoundException("There is no Such User"));
       
        Event event=eventRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("There is no Such Event"));
        Booking bookToSave= new Booking();
        bookToSave.setEvent(event);
        bookToSave.setSeats(dto.getSeats());
        bookToSave.setUser(user);
     
        if(!checkSeats(event,dto.getSeats())){
          
            throw new InsufficientSeatsException("there is no Enough Seats");
       }
      
       Booking book=bookingRepository.save(bookToSave);
        event.setAvailableSeats(event.getAvailableSeats()-dto.getSeats());
        eventRepository.save(event);
       
        return new BookingResponseDto(book.getId(),book.getEvent().getId(), book.getUser().getId(),book.getSeats());
    }

    public boolean checkSeats(Event event,int seats){
       if(event.getAvailableSeats()>=seats) return true;
        return false;
    }


}
