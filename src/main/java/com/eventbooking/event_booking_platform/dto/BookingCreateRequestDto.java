package com.eventbooking.event_booking_platform.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookingCreateRequestDto {

    @Min(1)
    private int seats;

    // Which tap this is. The browser mints it. A retry of that tap sends the same value.
    @NotBlank
    @Size(max = 64)
    private String clickId;
}
