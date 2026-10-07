package com.eventbooking.gateway;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.method;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

// Event's seat-reservations and holds/{holdId}/confirm are left out on purpose: only Booking calls them.
@Configuration
public class GatewayRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> authRoutes(@Value("${auth.service.uri}") String authUri) {
        return route("auth")
            .route(path("/api/auth/**", "/api/users/**"), http())
            .before(uri(authUri))
            .build();
    }

    // Exact paths, no /api/events/** wildcard, so Book and the internal Event endpoints don't match here.
    @Bean
    public RouterFunction<ServerResponse> eventRoutes(@Value("${event.service.uri}") String eventUri) {
        return route("event")
            .route(path("/api/events", "/api/events/{id}", "/api/venues/**"), http())
            .before(uri(eventUri))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> bookingRoutes(@Value("${booking.service.uri}") String bookingUri) {
        return route("booking")
            .route(path("/api/events/{eventId}/bookings").and(method(HttpMethod.POST)), http())
            .route(path("/api/bookings/**"), http())
            .before(uri(bookingUri))
            .build();
    }
}
