package com.eventbooking.event_booking_platform.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.dto.UserResponseDto;
import com.eventbooking.event_booking_platform.events.BookingCreatedEvent;
import com.eventbooking.event_booking_platform.model.Booking;
import com.eventbooking.event_booking_platform.model.BookingStatus;
import com.eventbooking.event_booking_platform.model.OutboxMessage;
import com.eventbooking.event_booking_platform.model.OutboxType;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.OutboxmessageRepository;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;

// Separate bean on purpose: @Transactional only runs through Spring's proxy, so a call from
// another method inside BookingServiceImpl (self-invocation) would get no transaction.
@Service 
public class BookingWriter {
 
    private final BookingRepository bookingRepository;
    private final MeterRegistry meterRegistry;
    private final ApplicationEventPublisher publisher;
    private final OutboxmessageRepository outboxMessage;
    public BookingWriter(
        BookingRepository bookingRepository,MeterRegistry meterRegistry,
        OutboxmessageRepository outboxMessage,ApplicationEventPublisher publisher){
       
        this.bookingRepository=bookingRepository;
        this.meterRegistry=meterRegistry;
        this.outboxMessage=outboxMessage;
        this.publisher=publisher;
    }

    @Transactional 
    public  BookingResponseDto writeBook(UserResponseDto resUser,int seats, EventResponseDto eventRes){
        Booking bookToSave= new Booking();

       bookToSave.setEventId(eventRes.getId());
       bookToSave.setSeats(seats);
      bookToSave.setUserId(resUser.getId());
      bookToSave.setEventTitle(eventRes.getTitle());
      bookToSave.setStatus(BookingStatus.CONFIRMED);
      Booking book=bookingRepository.save(bookToSave);
      meterRegistry.counter("bookings.created").increment();
      populateMessageAndSave( book);
      populateToConfirm(book,eventRes.getSeatHoldId());
      // Must stay inside this transaction: the listener is AFTER_COMMIT and only fires when there is a commit.
      publisher.publishEvent(new BookingCreatedEvent(book.getId()));
       return new BookingResponseDto(book.getId(),book.getEventId(), book.getUserId(),book.getSeats());

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
