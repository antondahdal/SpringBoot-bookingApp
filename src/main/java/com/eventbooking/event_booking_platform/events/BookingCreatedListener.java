package com.eventbooking.event_booking_platform.events;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.OutboxMessage;
import com.eventbooking.event_booking_platform.repository.OutboxmessageRepository;

@Component 
public class BookingCreatedListener {
    private final OutboxmessageRepository outboxMessage;
    public BookingCreatedListener(OutboxmessageRepository outboxMessage) {
        this.outboxMessage = outboxMessage;
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async 
    public void EventIdPublisher(BookingCreatedEvent  eventId){

       System.out.println(eventId.getId());
     upadteTheMessageBox(eventId.getId());

    }
    
    private  void upadteTheMessageBox(Long id){
        OutboxMessage outboxMessageTmp=outboxMessage.findByBookingId(id).orElseThrow(()->new ResourceNotFoundException("there isnt Such Message"));
        outboxMessageTmp.setStatus("SENT");
        outboxMessage.save(outboxMessageTmp);

    }
}
