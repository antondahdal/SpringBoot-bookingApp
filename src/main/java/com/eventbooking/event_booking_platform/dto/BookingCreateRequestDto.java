package com.eventbooking.event_booking_platform.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookingCreateRequestDto {

    @Min(1)
    private int seats;
}
