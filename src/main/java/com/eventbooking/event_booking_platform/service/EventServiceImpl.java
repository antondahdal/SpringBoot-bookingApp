package com.eventbooking.event_booking_platform.service;

import java.time.LocalDateTime;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.event_booking_platform.dto.EventCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.dto.EventUpdateRequestDto;
import com.eventbooking.event_booking_platform.exception.HoldDataExceedTimeException;
import com.eventbooking.event_booking_platform.exception.InsufficientSeatsException;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Event;
import com.eventbooking.event_booking_platform.model.HoldStatus;
import com.eventbooking.event_booking_platform.model.SeatHold;
import com.eventbooking.event_booking_platform.model.Venue;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.SeatHoldRepository;
import com.eventbooking.event_booking_platform.repository.VenueRepository;
@Service
public class EventServiceImpl implements EventService  {
    
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final SeatHoldRepository seatHoldRepository;

    public EventServiceImpl (EventRepository eventRepository,VenueRepository venueRepository,
        SeatHoldRepository seatHoldRepository
    ){
        this.eventRepository=eventRepository;
        this.venueRepository=venueRepository;
        this.seatHoldRepository=seatHoldRepository;
    }

    public EventResponseDto createEvent(EventCreateRequestDto dto){
        Venue venue = venueRepository.findById(dto.getVenueId()).orElseThrow(()->new ResourceNotFoundException("Venue Not Found "+dto.getVenueId()));
        Event saveEvent=new Event();
        saveEvent.setVenue(venue);
        saveEvent.setTitle(dto.getTitle());
        saveEvent.setDateTime(dto.getDateTime());
        saveEvent.setTotalSeats(dto.getTotalSeats());
        saveEvent.setAvailableSeats(dto.getTotalSeats());
        Event saved = eventRepository.save(saveEvent);
         return new EventResponseDto(
            saved.getId(),
            saved.getVenue().getId(),
            saved.getVenue().getName(),
            saved.getTitle(),
            saved.getDateTime(),
            saved.getTotalSeats(),
            saved.getAvailableSeats(),null);
    }

    @Cacheable("events")
    public EventResponseDto getEvent(Long id){
        Event returnedEvent= eventRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Event Not Found "+id));
        return new EventResponseDto(
            returnedEvent.getId(),
            returnedEvent.getVenue().getId(),
            returnedEvent.getVenue().getName(),
            returnedEvent.getTitle(),
            returnedEvent.getDateTime(),
            returnedEvent.getTotalSeats(),
            returnedEvent.getAvailableSeats(),null);
    }

    public Page<EventResponseDto> getAllEvents(Pageable pageable){
        return  eventRepository.findAll(pageable)
        .map( event -> new EventResponseDto(
            event.getId(),
            event.getVenue().getId(),
            event.getVenue().getName(),
            event.getTitle(),
            event.getDateTime(),
            event.getTotalSeats(),
            event.getAvailableSeats(),null
        ));
    }

    @Override
    public EventResponseDto updateEvent(Long id, EventUpdateRequestDto dto) {
        Event event =eventRepository.findById(id).orElseThrow(()->  new ResourceNotFoundException("there is no Such Event "));
       event.setTitle(dto.getTitle());
      Event returnedEvent= eventRepository.save(event);
       return new EventResponseDto(
        returnedEvent.getId(),
        returnedEvent.getVenue().getId(),
        returnedEvent.getVenue().getName(),
        returnedEvent.getTitle(),
        returnedEvent.getDateTime(),
        returnedEvent.getTotalSeats(),
        returnedEvent.getAvailableSeats(),null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "events", key = "#id")
    public EventResponseDto reserveSeats(Long id, Integer seats) {
        Event event=eventRepository.findByIdForUpdate(id).orElseThrow(()->new ResourceNotFoundException("There is no Such Event"));
        if(!checkSeats(event,seats)){
          
            throw new InsufficientSeatsException("there is no Enough Seats");
       }
       event.setAvailableSeats(event.getAvailableSeats()-seats);
       Event returnedEvent=eventRepository.save(event);
       SeatHold seatHold=new SeatHold();
       seatHold.setEvent(returnedEvent);
       seatHold.setSeats(seats);
       seatHold.setStatus(HoldStatus.HELD);
       seatHold.setExpiresAt(LocalDateTime.now().plusMinutes(10));
       SeatHold retSeatHold =seatHoldRepository.save(seatHold);
       return new EventResponseDto(
        returnedEvent.getId(),
        returnedEvent.getVenue().getId(),
        returnedEvent.getVenue().getName(),
        returnedEvent.getTitle(),
        returnedEvent.getDateTime(),
        returnedEvent.getTotalSeats(),
        returnedEvent.getAvailableSeats(),        retSeatHold.getId());
    
    }

    public boolean checkSeats(Event event,int seats){
        if(event.getAvailableSeats()>=seats) return true;
         return false;
     }
     @Override 
     @Transactional 
     public void confirmHold(Long holdId){
        SeatHold retSeatHold =seatHoldRepository.findByIdForUpdate(holdId).orElseThrow(()->new ResourceNotFoundException("there is no Hold "));
        if(HoldStatus.HELD.equals(retSeatHold.getStatus())){
        

                retSeatHold.setStatus(HoldStatus.CONFIRMED);
                seatHoldRepository.save(retSeatHold);
        }
            if(HoldStatus.EXPIRED.equals(retSeatHold.getStatus())) throw new HoldDataExceedTimeException("Expired Seats");

          
        }

        @Override 
        @Transactional  
        public void expireHold(Long holdId){
            SeatHold retSeatHold =seatHoldRepository.findByIdForUpdate(holdId).orElseThrow(()->new ResourceNotFoundException("there is no Hold "));
            if(HoldStatus.HELD.equals(retSeatHold.getStatus())){
            
                Event event=eventRepository.findByIdForUpdate(retSeatHold.getEvent().getId()).orElseThrow(()->new ResourceNotFoundException("There is no Such Event"));
                event.setAvailableSeats(event.getAvailableSeats()+retSeatHold.getSeats());
                eventRepository.save(event);
                retSeatHold.setStatus(HoldStatus.EXPIRED);
                    seatHoldRepository.save(retSeatHold);
            }
        }
        
    

}
