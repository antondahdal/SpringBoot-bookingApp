package com.eventbooking.event_booking_platform.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventCreateRequestDto {
   @NotNull
   private Long venueId;
    @NotBlank
    private  String title;
    @NotNull
    private  LocalDateTime dateTime;
    @Min(1)
    private  int totalSeats;



}
