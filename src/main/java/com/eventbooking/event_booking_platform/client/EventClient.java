package com.eventbooking.event_booking_platform.client;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.eventbooking.event_booking_platform.dto.EventResponseDto;
import com.eventbooking.event_booking_platform.dto.SeatReservationRequestDto;
import com.eventbooking.event_booking_platform.exception.DownstreamServiceException;
import com.eventbooking.event_booking_platform.exception.HoldDataExceedTimeException;
import com.eventbooking.event_booking_platform.exception.InsufficientSeatsException;
import com.eventbooking.event_booking_platform.exception.ResourceNotFoundException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Component
public class EventClient {
    
    private final WebClient webClient;
    private final String internalToken;

    public EventClient(@Qualifier("eventWebClient") WebClient webClient,
            @Value("${app.internal.token}") String internalToken){
        this.webClient=webClient;
        this.internalToken=internalToken;
    }

    @CircuitBreaker(name = "event" , fallbackMethod = "reserveSeatsFallback")
    
    public EventResponseDto reserveSeats(Long eventId, int seats){
        SeatReservationRequestDto dto = new SeatReservationRequestDto();
        dto.setSeats(seats);
        ServletRequestAttributes attrs =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String authHeader = attrs.getRequest().getHeader("Authorization");
        String correlationId = (String) attrs.getRequest().getAttribute("X-Correlation-Id");
        EventResponseDto resDto=null;
        try {
             resDto=  webClient.post().uri("/api/events/{id}/seat-reservations", eventId)
      .header("Authorization", authHeader)
      .header("X-Correlation-Id", correlationId)
      .bodyValue(dto ).retrieve().bodyToMono(EventResponseDto.class).block();
            
        } catch (WebClientResponseException  e) {
            if(e.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)){
                throw new InsufficientSeatsException ("there is no Seat left");
            }
            if(e.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)){
                throw new ResourceNotFoundException ("there is no Such Event");
            }
            if(e.getStatusCode().is5xxServerError()){
                throw new DownstreamServiceException ("Issue In Server Try Again Later !");
            }

            throw e;
            
        }catch (WebClientRequestException  e){
            throw new DownstreamServiceException ("Issue In Server Try Again Later !");
        }
      return resDto;
    }

    private EventResponseDto reserveSeatsFallback(Long eventId, int seats, Throwable throwable) throws Throwable{
        if (throwable instanceof InsufficientSeatsException||throwable instanceof ResourceNotFoundException) throw throwable;
        if (throwable instanceof CallNotPermittedException ) throw throwable; 
        throw new DownstreamServiceException("There is internal err");
    
}

public void confirmHold(Long holdId){

    try {
    webClient.post().uri("/api/events/holds/{holdId}/confirm", holdId)
        .header("X-Internal-Token", internalToken)
        .retrieve().toBodilessEntity().block();
    }
    catch(WebClientResponseException e){
        if(e.getStatusCode().isSameCodeAs(HttpStatus.CONFLICT)){
            throw new HoldDataExceedTimeException ("Faild to Update the Event to Hold ");
        }
        if(e.getStatusCode().is5xxServerError()){
            throw new DownstreamServiceException ("Issue In Server Try Again Later !");
        }

        throw e;
        
    }catch (WebClientRequestException  e){
        throw new DownstreamServiceException ("Issue In Server Try Again Later !");
    }
    
}
}