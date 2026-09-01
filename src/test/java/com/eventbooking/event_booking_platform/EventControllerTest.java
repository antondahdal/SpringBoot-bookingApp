package com.eventbooking.event_booking_platform;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.eventbooking.event_booking_platform.controller.EventController;
import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.service.EventService;

@WebMvcTest(EventController.class)
public class EventControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private EventService eventService;
    
    @Test
void getEventById_returns200AndBody() throws Exception {
    EventResponseDto dto = new EventResponseDto(
        1L, 10L, "Arena", "Concert",
        LocalDateTime.of(2026, 9, 1, 20, 0),
        100, 80
    );
    when(eventService.getEvent(1L)).thenReturn(dto);
    mockMvc.perform(get("/api/events/1"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id").value(1))
    .andExpect(jsonPath("$.title").value("Concert"));
    
}

    
}
