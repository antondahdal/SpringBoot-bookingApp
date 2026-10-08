package com.eventbooking.events.dto;

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
