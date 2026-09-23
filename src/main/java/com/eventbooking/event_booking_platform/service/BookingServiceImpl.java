package com.eventbooking.event_booking_platform.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import com.eventbooking.event_booking_platform.dto.BookingCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.dto.UserResponseDto;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Booking;
import com.eventbooking.event_booking_platform.model.Event;
import com.eventbooking.event_booking_platform.model.OutboxMessage;
import com.eventbooking.event_booking_platform.model.User;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.OutboxmessageRepository;
import com.eventbooking.event_booking_platform.repository.UserRepository;

import io.micrometer.core.instrument.MeterRegistry;

import com.eventbooking.event_booking_platform.client.AuthClient;
import com.eventbooking.event_booking_platform.client.EventClient;
import com.eventbooking.event_booking_platform.events.BookingCreatedEvent;
import jakarta.transaction.Transactional;
@Service
public class BookingServiceImpl implements BookingService {
    private final ApplicationEventPublisher publisher;
    private final BookingRepository bookingRepository;
    private final  UserRepository userRepository;
    private final   EventRepository eventRepository;
    private final   EventClient eventClient;
    private final AuthClient authClient;
    private final OutboxmessageRepository outboxMessage;
    private final MeterRegistry meterRegistry;

    public BookingServiceImpl( BookingRepository bookingRepository,
        UserRepository userRepository,
        EventRepository eventRepository,EventClient eventClient,
        AuthClient authClient,ApplicationEventPublisher publisher,OutboxmessageRepository outboxMessage, MeterRegistry meterRegistry){
            this.bookingRepository=bookingRepository;
            this.userRepository=userRepository;
            this.eventRepository=eventRepository;
            this.eventClient=eventClient;
            this.authClient=authClient;
            this.publisher=publisher;
            this.outboxMessage=outboxMessage;
            this.meterRegistry=meterRegistry;


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
       meterRegistry.counter("bookings.created").increment();
       populateMessageAndSave( book);
       publisher.publishEvent(new BookingCreatedEvent(book.getId()));
        return new BookingResponseDto(book.getId(),book.getEvent().getId(), book.getUser().getId(),book.getSeats());
    }

private void populateMessageAndSave(Booking book){
    OutboxMessage mess=new OutboxMessage();
    mess.setBookingId(book.getId());
    mess.setStatus("PENDING");
    outboxMessage.save(mess);
}
   

}
