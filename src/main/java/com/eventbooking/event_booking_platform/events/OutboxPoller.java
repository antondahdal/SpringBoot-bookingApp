package com.eventbooking.event_booking_platform.events;

import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.OutboxMessage;
import com.eventbooking.event_booking_platform.repository.OutboxmessageRepository;
import com.eventbooking.event_booking_platform.service.BookingNotifier;

@Component 
public class OutboxPoller {
    OutboxmessageRepository outboxmessageRepository;
    BookingNotifier bookingNotifier;
    
    
public  OutboxPoller( OutboxmessageRepository outboxmessageRepository,
    BookingNotifier bookingNotifier){
this.outboxmessageRepository=outboxmessageRepository;
this.bookingNotifier=bookingNotifier;
}

@Scheduled (fixedDelay = 10000)
public void checkMail(){
List<OutboxMessage> pendinMessages=outboxmessageRepository.findByStatus("PENDING");
for(OutboxMessage message:pendinMessages){
    bookingNotifier.send(message.getBookingId());
    upadteTheMessageBox(message.getBookingId());

}
}

private  void upadteTheMessageBox(Long id){
    OutboxMessage outboxMessageTmp=outboxmessageRepository.findByBookingId(id).orElseThrow(()->new ResourceNotFoundException("there isnt Such Message"));
    outboxMessageTmp.setStatus("SENT");
    outboxmessageRepository.save(outboxMessageTmp);

}


}


