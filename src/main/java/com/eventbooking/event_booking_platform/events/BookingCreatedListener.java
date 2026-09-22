package com.eventbooking.event_booking_platform.events;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.OutboxMessage;
import com.eventbooking.event_booking_platform.repository.OutboxmessageRepository;
import com.eventbooking.event_booking_platform.service.BookingNotifier;

@Component 
public class BookingCreatedListener {
    private final OutboxmessageRepository outboxMessage;
    private final BookingNotifier bookingNotifier;
    public BookingCreatedListener(OutboxmessageRepository outboxMessage,BookingNotifier bookingNotifier) {
        this.outboxMessage = outboxMessage;
        this.bookingNotifier=bookingNotifier;
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async 
    public void EventIdPublisher(BookingCreatedEvent  eventId){

       bookingNotifier.send(eventId.getId());
     upadteTheMessageBox(eventId.getId());

    }
    
    private  void upadteTheMessageBox(Long id){
        OutboxMessage outboxMessageTmp=outboxMessage.findByBookingId(id).orElseThrow(()->new ResourceNotFoundException("there isnt Such Message"));
        outboxMessageTmp.setStatus("SENT");
        outboxMessage.save(outboxMessageTmp);

    }
}
