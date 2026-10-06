package com.eventbooking.event_booking_platform.events;

import java.net.http.WebSocketHandshakeException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.eventbooking.event_booking_platform.client.EventClient;
import com.eventbooking.event_booking_platform.exception.DownstreamServiceException;
import com.eventbooking.event_booking_platform.exception.HoldDataExceedTimeException;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Booking;
import com.eventbooking.event_booking_platform.model.BookingStatus;
import com.eventbooking.event_booking_platform.model.OutboxMessage;
import com.eventbooking.event_booking_platform.model.OutboxType;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.OutboxmessageRepository;
import com.eventbooking.event_booking_platform.service.BookingNotifier;

@Component 
public class OutboxPoller {
 private   OutboxmessageRepository outboxmessageRepository;
 private EventClient eventClient;
   private  BookingNotifier bookingNotifier;
   private BookingRepository bookingRepository;
    
    
public  OutboxPoller( OutboxmessageRepository outboxmessageRepository,
    BookingNotifier bookingNotifier,EventClient eventClient,BookingRepository bookingRepository){
this.outboxmessageRepository=outboxmessageRepository;
this.bookingNotifier=bookingNotifier;
this.eventClient=eventClient;
this.bookingRepository=bookingRepository;
}

@Scheduled (fixedDelay = 10000)
public void poller (){
List<OutboxMessage> pendinMessages=outboxmessageRepository.findByStatus("PENDING");

    for(OutboxMessage message:pendinMessages){
        try {
        if(message.getType().equals(OutboxType.NOTIFY)){
            bookingNotifier.send(message.getBookingId());
        }
        if(message.getType().equals(OutboxType.CONFIRM_HOLD)){
            eventClient.confirmHold(message.getHoldId());
        }
        upadteTheMessageBox(message.getBookingId(),message.getType());
    
}
catch (DownstreamServiceException e) {
    

}
catch(HoldDataExceedTimeException e){
    Booking book=bookingRepository.findById(message.getBookingId()).orElseThrow();
    book.setStatus(BookingStatus.CANCELLED);
    bookingRepository.save(book);
    upadteTheMessageBox(message.getBookingId(),message.getType());


}
    }
}

private  void upadteTheMessageBox(Long id,OutboxType type){
    OutboxMessage outboxMessageTmp=null;
if(type.equals(OutboxType.NOTIFY)){
  outboxMessageTmp= outboxmessageRepository.findByBookingIdAndType(id,OutboxType.NOTIFY).orElseThrow(()->new ResourceNotFoundException("there isnt Such Message"));

}if(type.equals(OutboxType.CONFIRM_HOLD)){
    outboxMessageTmp= outboxmessageRepository.findByBookingIdAndType(id,OutboxType.CONFIRM_HOLD).orElseThrow(()->new ResourceNotFoundException("there isnt Such Message"));

}

outboxMessageTmp.setStatus("SENT");
outboxmessageRepository.save(outboxMessageTmp);

}

}


