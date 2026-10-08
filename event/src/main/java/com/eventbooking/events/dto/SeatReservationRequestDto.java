package com.eventbooking.events.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SeatReservationRequestDto {
    @Min(1)
    private int seats;
}
