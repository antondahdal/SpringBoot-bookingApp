package com.eventbooking.event_booking_platform.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.dto.MyBookingResponseDto;
import com.eventbooking.event_booking_platform.dto.UserResponseDto;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.client.AuthClient;
import com.eventbooking.event_booking_platform.client.EventClient;
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final   EventClient eventClient;
    private final AuthClient authClient;
    private final BookingWriter bookingWriter;
   

    public BookingServiceImpl(BookingRepository bookingRepository,EventClient eventClient,AuthClient authClient,BookingWriter bookingWriter){
            this.bookingRepository=bookingRepository;
            this.eventClient=eventClient;
            this.authClient=authClient;
            this.bookingWriter=bookingWriter;


    } 
    
    
    // Not @Transactional: a transaction borrows a DB connection when it starts, and it must not be held
    // while waiting on the Auth and Event HTTP calls. Only BookingWriter.writeBook opens one.
    public BookingResponseDto book (BookingCreateRequestDto dto,long id){
        UserResponseDto resUser= authClient.checkIfExist();
      EventResponseDto eventRes= eventClient.reserveSeats(id, dto.getSeats());
       return bookingWriter.writeBook(resUser, dto.getSeats(),eventRes);
          }

    // Not @Transactional for the same reason as book(). 
    public List<MyBookingResponseDto> myBookings(){
        UserResponseDto resUser= authClient.checkIfExist();
        return bookingRepository.findByUserId(resUser.getId()).stream()
            .map(b -> new MyBookingResponseDto(b.getId(), b.getEventTitle(), b.getSeats()))
            .toList();
    }



}
