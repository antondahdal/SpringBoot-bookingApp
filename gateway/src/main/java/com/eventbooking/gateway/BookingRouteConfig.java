package com.eventbooking.gateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.method;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration 
public class BookingRouteConfig {

    
@Bean 
    public RouterFunction<ServerResponse> bookingRoute(@Value("${booking.service.uri}")  String bookingUri){

        return route("booking")
        .route(path("/api/events/{eventId}/bookings").and(method(HttpMethod.POST)), http(bookingUri))
        .build();
    
    
    }
}
