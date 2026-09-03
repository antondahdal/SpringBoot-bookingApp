package com.eventbooking.event_booking_platform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.event_booking_platform.dto.EventCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.dto.EventUpdateRequestDto;
import com.eventbooking.event_booking_platform.exception.InsufficientSeatsException;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Event;
import com.eventbooking.event_booking_platform.model.Venue;
import com.eventbooking.event_booking_platform.repository.EventRepository;
import com.eventbooking.event_booking_platform.repository.VenueRepository;
@Service
public class EventServiceImpl implements EventService  {
    
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    public EventServiceImpl (EventRepository eventRepository,VenueRepository venueRepository){
        this.eventRepository=eventRepository;
        this.venueRepository=venueRepository;
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
            saved.getAvailableSeats());
    }

    public EventResponseDto getEvent(Long id){
        Event returnedEvent= eventRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Event Not Found "+id));
        return new EventResponseDto(
            returnedEvent.getId(),
            returnedEvent.getVenue().getId(),
            returnedEvent.getVenue().getName(),
            returnedEvent.getTitle(),
            returnedEvent.getDateTime(),
            returnedEvent.getTotalSeats(),
            returnedEvent.getAvailableSeats());
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
            event.getAvailableSeats()
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
        returnedEvent.getAvailableSeats());
    }

    @Override
    @Transactional
    public EventResponseDto reserveSeats(Long id, Integer seats) {
        Event event=eventRepository.findByIdForUpdate(id).orElseThrow(()->new ResourceNotFoundException("There is no Such Event"));
        if(!checkSeats(event,seats)){
          
            throw new InsufficientSeatsException("there is no Enough Seats");
       }
       event.setAvailableSeats(event.getAvailableSeats()-seats);
       Event returnedEvent=eventRepository.save(event);
       return new EventResponseDto(
        returnedEvent.getId(),
        returnedEvent.getVenue().getId(),
        returnedEvent.getVenue().getName(),
        returnedEvent.getTitle(),
        returnedEvent.getDateTime(),
        returnedEvent.getTotalSeats(),
        returnedEvent.getAvailableSeats());
    
    }

    public boolean checkSeats(Event event,int seats){
        if(event.getAvailableSeats()>=seats) return true;
         return false;
     }

}
