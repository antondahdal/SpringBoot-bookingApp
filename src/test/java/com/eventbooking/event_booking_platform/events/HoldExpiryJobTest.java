package com.eventbooking.event_booking_platform.events;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eventbooking.event_booking_platform.model.HoldStatus;
import com.eventbooking.event_booking_platform.model.SeatHold;
import com.eventbooking.event_booking_platform.repository.SeatHoldRepository;
import com.eventbooking.event_booking_platform.service.EventService;

@ExtendWith(MockitoExtension.class)
class HoldExpiryJobTest {

    @Mock
    private SeatHoldRepository seatHoldRepository;

    @Mock
    private EventService eventService;

    @InjectMocks
    private HoldExpiryJob holdExpiryJob;

    @Test
    void expireEvents_expiresEveryOverdueHeldHold() {
        SeatHold first = new SeatHold();
        first.setId(1L);
        SeatHold second = new SeatHold();
        second.setId(2L);

        when(seatHoldRepository.findByStatusAndExpiresAtBefore(eq(HoldStatus.HELD), any(LocalDateTime.class)))
                .thenReturn(List.of(first, second));

        holdExpiryJob.expireEvents();

        verify(eventService).expireHold(1L);
        verify(eventService).expireHold(2L);
    }

    @Test
    void expireEvents_doesNothingWhenNoHoldIsOverdue() {
        when(seatHoldRepository.findByStatusAndExpiresAtBefore(eq(HoldStatus.HELD), any(LocalDateTime.class)))
                .thenReturn(List.of());

        holdExpiryJob.expireEvents();

        verify(eventService, never()).expireHold(any());
    }
}
