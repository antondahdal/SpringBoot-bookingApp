package com.eventbooking.event_booking_platform.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eventbooking.event_booking_platform.client.EventClient;
import com.eventbooking.event_booking_platform.exception.HoldDataExceedTimeException;
import com.eventbooking.event_booking_platform.model.Booking;
import com.eventbooking.event_booking_platform.model.BookingStatus;
import com.eventbooking.event_booking_platform.model.OutboxMessage;
import com.eventbooking.event_booking_platform.model.OutboxType;
import com.eventbooking.event_booking_platform.repository.BookingRepository;
import com.eventbooking.event_booking_platform.repository.OutboxmessageRepository;
import com.eventbooking.event_booking_platform.service.BookingNotifier;

@ExtendWith(MockitoExtension.class)
class OutboxPollerTest {

    @Mock
    private OutboxmessageRepository outboxmessageRepository;

    @Mock
    private BookingNotifier bookingNotifier;

    @Mock
    private EventClient eventClient;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private OutboxPoller outboxPoller;

    @Test
    void poller_sendsPendingNotifyAndMarksSent() {
        OutboxMessage message = new OutboxMessage();
        message.setId(7L);
        message.setBookingId(42L);
        message.setType(OutboxType.NOTIFY);
        message.setStatus("PENDING");

        when(outboxmessageRepository.findByStatus("PENDING")).thenReturn(List.of(message));
        when(outboxmessageRepository.findByBookingIdAndType(42L, OutboxType.NOTIFY)).thenReturn(Optional.of(message));

        outboxPoller.poller();

        verify(bookingNotifier).send(42L);
        assertEquals("SENT", message.getStatus());
        verify(outboxmessageRepository).save(message);
    }

    @Test
    void poller_cancelsBookingWhenHoldAlreadyExpired() {
        OutboxMessage message = new OutboxMessage();
        message.setId(8L);
        message.setBookingId(42L);
        message.setHoldId(5L);
        message.setType(OutboxType.CONFIRM_HOLD);
        message.setStatus("PENDING");

        Booking booking = new Booking();
        booking.setId(42L);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(outboxmessageRepository.findByStatus("PENDING")).thenReturn(List.of(message));
        doThrow(new HoldDataExceedTimeException("Expired Seats")).when(eventClient).confirmHold(5L);
        when(bookingRepository.findById(42L)).thenReturn(Optional.of(booking));
        when(outboxmessageRepository.findByBookingIdAndType(42L, OutboxType.CONFIRM_HOLD)).thenReturn(Optional.of(message));

        outboxPoller.poller();

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        verify(bookingRepository).save(booking);
        assertEquals("SENT", message.getStatus());
    }
}
