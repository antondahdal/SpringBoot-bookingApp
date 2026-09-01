package com.eventbooking.event_booking_platform.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.eventbooking.event_booking_platform.dto.VenueCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.VenueResponseDto;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;
import com.eventbooking.event_booking_platform.model.Venue;
import com.eventbooking.event_booking_platform.repository.VenueRepository;

@Service
public class VenueServiceImpl implements VenueService {
    
    private final VenueRepository venueRepository;
    public VenueServiceImpl(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    public VenueResponseDto createVenue(VenueCreateRequestDto dto){
        Venue newRow=new Venue();
        newRow.setAddress(dto.getAddress());
        newRow.setCapacity(dto.getCapacity());
        newRow.setName(dto.getName());
        Venue ret=  venueRepository.save(newRow);

        VenueResponseDto retResp=new VenueResponseDto(ret.getId(),ret.getName(),ret.getAddress(),ret.getCapacity());
        
        return retResp;
    }

    public VenueResponseDto getVenue(Long id){
        Venue returnedVenue= venueRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Venue Not Found "+id));
        VenueResponseDto retResp=new VenueResponseDto(returnedVenue.getId(),returnedVenue.getName(),returnedVenue.getAddress(),returnedVenue.getCapacity());
        return retResp;
    }

    public List<VenueResponseDto> getAllVenues() {
        return venueRepository.findAll()
                .stream()
                .map(venue -> new VenueResponseDto(
                        venue.getId(),
                        venue.getName(),
                        venue.getAddress(),
                        venue.getCapacity()))
                .toList();
    }

}
