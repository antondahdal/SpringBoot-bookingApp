package com.eventbooking.event_booking_platform;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eventbooking.event_booking_platform.controller.BookingController;
import com.eventbooking.event_booking_platform.dto.BookingResponseDto;
import com.eventbooking.event_booking_platform.exception.GlobalExceptionHandler;
import com.eventbooking.event_booking_platform.exception.InsufficientSeatsException;
import com.eventbooking.event_booking_platform.service.BookingService;

@WebMvcTest(BookingController.class)
 @Import(GlobalExceptionHandler.class)
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private BookingService bookingService;

    @Test
   @WithMockUser(roles = "ATTENDEE")
    void doBooking() throws Exception {
        when(bookingService.book(any(), eq(1L)))
            .thenReturn(new BookingResponseDto(1L, 1L, 10L, 2));

        mockMvc.perform(post("/api/events/1/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"seats\": 2}"))
            .andExpect(status().isCreated());
    }

    @Test
    void doBooking1() throws Exception {
    

        mockMvc.perform(post("/api/events/1/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"seats\": 2}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ATTENDEE")
    void doInvalidBooking() throws Exception {
        when(bookingService.book(any(), eq(1L)))
        .thenThrow(new InsufficientSeatsException("no seats"));

    mockMvc.perform(post("/api/events/1/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"seats\": 2}"))
            .andExpect(status().isConflict());
    }
    }
    

