package com.eventbooking.event_booking_platform.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.dto.MyBookingResponseDto;
import com.eventbooking.event_booking_platform.dto.UserResponseDto;
import com.eventbooking.event_booking_platform.client.AuthClient;
import com.eventbooking.event_booking_platform.client.EventClient;
import com.eventbooking.event_booking_platform.exception.ClickIdMismatchException;
import com.eventbooking.event_booking_platform.model.Booking;
@Service
public class BookingServiceImpl implements BookingService {
    private final   EventClient eventClient;
    private final AuthClient authClient;
    private final BookingWriter bookingWriter;
   

    public BookingServiceImpl(EventClient eventClient,AuthClient authClient,BookingWriter bookingWriter){
            this.eventClient=eventClient;
            this.authClient=authClient;
            this.bookingWriter=bookingWriter;


    } 
    
    
    // Not @Transactional: a transaction borrows a DB connection when it starts, and it must not be held
    // while waiting on the Auth and Event HTTP calls. Only BookingWriter.writeBook opens one.
    public BookingResponseDto book (BookingCreateRequestDto dto,long id){
        UserResponseDto resUser= authClient.checkIfExist();
        Optional<Booking> already = bookingWriter.findByClickId(dto.getClickId());
        if (already.isPresent()) {
            return replay(already.get(), resUser, id, dto);
        }
        EventResponseDto eventRes= eventClient.reserveSeats(id, dto.getSeats(), dto.getClickId());
        try {
            return bookingWriter.writeBook(resUser, dto.getSeats(), eventRes, dto.getClickId());
        } catch (DataIntegrityViolationException ex) {
            // The other try of this tap inserted the row first. Return that ticket.
            return bookingWriter.findByClickId(dto.getClickId())
                .map(found -> replay(found, resUser, id, dto))
                .orElseThrow(() -> ex);
        }
    }

    private BookingResponseDto replay(Booking found, UserResponseDto user, long eventId, BookingCreateRequestDto dto) {
        if (!found.getUserId().equals(user.getId()) || !found.getEventId().equals(eventId) || found.getSeats() != dto.getSeats()) {
            throw new ClickIdMismatchException("This click id was already used for a different book.");
        }
        return new BookingResponseDto(found.getId(), found.getEventId(), found.getUserId(), found.getSeats(), found.getClickId());
    }

    // Not @Transactional for the same reason as book(). 
    public List<MyBookingResponseDto> myBookings(){
        UserResponseDto resUser= authClient.checkIfExist();
        return bookingWriter.findByUserId(resUser.getId()).stream()
            .map(b -> new MyBookingResponseDto(b.getId(), b.getEventTitle(), b.getSeats()))
            .toList();
    }



}
