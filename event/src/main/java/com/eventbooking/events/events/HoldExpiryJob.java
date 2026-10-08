package com.eventbooking.events.events;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventbooking.events.model.HoldStatus;
import com.eventbooking.events.model.SeatHold;
import com.eventbooking.events.repository.SeatHoldRepository;
import com.eventbooking.events.service.EventService;

@Component 
public class HoldExpiryJob {

    private final SeatHoldRepository seatHoldRepository;
    private final EventService eventService;
    public HoldExpiryJob(SeatHoldRepository seatHoldRepository,EventService eventService){
        this.seatHoldRepository=   seatHoldRepository;
        this.eventService=eventService;

    }

    @Scheduled (fixedDelay=5000)
    public void expireEvents(){
  List<SeatHold> events1=seatHoldRepository.findByStatusAndExpiresAtBefore(HoldStatus.HELD, LocalDateTime.now());
    
        for(SeatHold seat : events1){

    eventService.expireHold(seat.getId());
        }

}
    
}
