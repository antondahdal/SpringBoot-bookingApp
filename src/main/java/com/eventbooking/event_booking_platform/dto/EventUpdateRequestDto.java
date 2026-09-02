package com.eventbooking.event_booking_platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class EventUpdateRequestDto {

    @NotBlank
 private String title;

    
}
