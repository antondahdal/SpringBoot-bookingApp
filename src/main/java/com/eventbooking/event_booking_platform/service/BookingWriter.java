package com.eventbooking.event_booking_platform.service;

import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(propagation = Propagation.REQUIRED)
    public  BookingResponseDto writeBook(UserResponseDto resUser,int seats, EventResponseDto eventRes, String clickId){
        Booking bookToSave= new Booking();

       bookToSave.setEventId(eventRes.getId());
       bookToSave.setSeats(seats);
      bookToSave.setUserId(resUser.getId());
      bookToSave.setEventTitle(eventRes.getTitle());
      bookToSave.setStatus(BookingStatus.CONFIRMED);
      bookToSave.setClickId(clickId);
      Booking book=bookingRepository.save(bookToSave);
      meterRegistry.counter("bookings.created").increment();
      populateMessageAndSave( book);
      populateToConfirm(book,eventRes.getSeatHoldId());
      // Must stay inside this transaction: the listener is AFTER_COMMIT and only fires when there is a commit.
      publisher.publishEvent(new BookingCreatedEvent(book.getId()));
       return new BookingResponseDto(book.getId(),book.getEventId(), book.getUserId(),book.getSeats(), book.getClickId());

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

    @Transactional(readOnly = true)
    public List<Booking> findByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<Booking> findByClickId(String clickId) {
        return bookingRepository.findByClickId(clickId);
    }
}
