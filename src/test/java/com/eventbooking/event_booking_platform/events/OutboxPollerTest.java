package com.eventbooking.event_booking_platform.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eventbooking.event_booking_platform.model.OutboxMessage;
import com.eventbooking.event_booking_platform.repository.OutboxmessageRepository;
import com.eventbooking.event_booking_platform.service.BookingNotifier;

@ExtendWith(MockitoExtension.class)
class OutboxPollerTest {

    @Mock
    private OutboxmessageRepository outboxmessageRepository;

    @Mock
    private BookingNotifier bookingNotifier;

    @InjectMocks
    private OutboxPoller outboxPoller;

    @Test
    void checkMail_sendsPendingByBookingIdAndMarksSent() {
        OutboxMessage message = new OutboxMessage();
        message.setId(7L);
        message.setBookingId(42L);
        message.setStatus("PENDING");

        when(outboxmessageRepository.findByStatus("PENDING")).thenReturn(List.of(message));
        when(outboxmessageRepository.findByBookingId(42L)).thenReturn(Optional.of(message));

        outboxPoller.checkMail();

        verify(bookingNotifier).send(42L);
        assertEquals("SENT", message.getStatus());
        verify(outboxmessageRepository).save(message);
    }
}
