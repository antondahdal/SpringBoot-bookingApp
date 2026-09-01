package com.eventbooking.event_booking_platform.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventbooking.event_booking_platform.dto.VenueCreateRequestDto;
import com.eventbooking.event_booking_platform.dto.VenueResponseDto;
import com.eventbooking.event_booking_platform.service.VenueService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/venues")
public class VenueController {
    private final VenueService venueService;
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }
    @PostMapping
    public ResponseEntity<VenueResponseDto> createVenue(@Valid @RequestBody VenueCreateRequestDto dto) {
        VenueResponseDto response = venueService.createVenue(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDto> getVenueById(@PathVariable Long id){
        VenueResponseDto response=venueService.getVenue(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }


    @GetMapping
public ResponseEntity<List<VenueResponseDto>> getAllVenues() {
    return ResponseEntity.ok(venueService.getAllVenues());
}
}