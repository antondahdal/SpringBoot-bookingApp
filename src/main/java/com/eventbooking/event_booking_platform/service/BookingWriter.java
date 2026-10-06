package com.eventbooking.event_booking_platform.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.dto.UserResponseDto;
import com.eventbooking.event_booking_platform.events.BookingCreatedEvent;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Booking;
import com.eventbooking.event_booking_platform.model.BookingStatus;
import com.eventbooking.event_booking_platform.model.Event;
import com.eventbooking.event_booking_platform.model.OutboxMessage;
import com.eventbooking.event_booking_platform.model.OutboxType;
import com.eventbooking.event_booking_platform.model.User;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.OutboxmessageRepository;
import com.eventbooking.event_booking_platform.repository.UserRepository;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;

// Separate bean on purpose: @Transactional only runs through Spring's proxy, so a call from
// another method inside BookingServiceImpl (self-invocation) would get no transaction.
@Service 
public class BookingWriter {
    private final UserRepository userRepository;
    private final   EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final MeterRegistry meterRegistry;
    private final ApplicationEventPublisher publisher;
    private final OutboxmessageRepository outboxMessage;
    public BookingWriter(UserRepository userRepository,EventRepository eventRepository,
        BookingRepository bookingRepository,MeterRegistry meterRegistry,
        OutboxmessageRepository outboxMessage,ApplicationEventPublisher publisher){
        this.userRepository=userRepository;
        this.eventRepository=eventRepository;
        this.bookingRepository=bookingRepository;
        this.meterRegistry=meterRegistry;
        this.outboxMessage=outboxMessage;
        this.publisher=publisher;
    }

    @Transactional 
    public  BookingResponseDto writeBook(UserResponseDto resUser,long id,int seats,long holdaid){
        Booking bookToSave= new Booking();

       User user =userRepository.findById(resUser.getId()).orElseThrow(()->new ResourceNotFoundException("User Not Found "));
       Event eventToBook=eventRepository.getReferenceById(id);
       bookToSave.setEvent(eventToBook);
       bookToSave.setSeats(seats);
      bookToSave.setUser(user);
      bookToSave.setStatus(BookingStatus.CONFIRMED);
      Booking book=bookingRepository.save(bookToSave);
      meterRegistry.counter("bookings.created").increment();
      populateMessageAndSave( book);
      populateToConfirm(book,holdaid);
      // Must stay inside this transaction: the listener is AFTER_COMMIT and only fires when there is a commit.
      publisher.publishEvent(new BookingCreatedEvent(book.getId()));
       return new BookingResponseDto(book.getId(),book.getEvent().getId(), book.getUser().getId(),book.getSeats());

    }
    private void populateMessageAndSave(Booking book){
        OutboxMessage mess=new OutboxMessage();
        mess.setType(OutboxType.NOTIFY);
        mess.setBookingId(book.getId());
        mess.setStatus("PENDING");
        outboxMessage.save(mess);
    }
    private void populateToConfirm(Booking book,Long holdaid){
        OutboxMessage mess=new OutboxMessage();
        mess.setType(OutboxType.CONFIRM_HOLD);
        mess.setHoldId(holdaid);
        mess.setBookingId(book.getId());
        mess.setStatus("PENDING");
        outboxMessage.save(mess);
    }
       
    
}
