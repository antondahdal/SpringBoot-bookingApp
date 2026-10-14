package com.eventbooking.gateway;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Extra Book taps stop here. They never reach book() and never lock the seat row.
// Sold out is still 409 from Event, after a tap this filter let through.
//
// Lab only. Weak points to say in an interview:
// - Count never resets until the gateway process restarts (no time window).
// - Map is in this JVM. Two gateway pods = two separate counters.
// - Key is remote IP, not click id and not the logged-in user. A café NAT shares one bucket.
// - A million different IPs stay in RAM forever.
// Production: Redis (or gateway rate-limiter) with a window; click id on Booking for the same tap.
@Component
public class BookRateLimitFilter extends OncePerRequestFilter {

    private static final int MAX = 5;
    // Lives for the life of this bean. Not cleared when the HTTP call returns.
    private final ConcurrentHashMap<String, AtomicInteger> taps = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (isBookPost(request)) {
            // remoteAddr = IP the gateway sees. Not click id. Same tap twice still two counts.
            int n = taps.computeIfAbsent(request.getRemoteAddr(), k -> new AtomicInteger()).incrementAndGet();
            if (n > MAX) {
                response.setStatus(429);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isBookPost(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().matches("/api/events/[^/]+/bookings");
    }
}
