package com.eventbooking.gateway;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Extra Book taps stop here. They never reach book() and never lock the seat row.
// The jar holds a few tokens. One Book spends one. Tokens drip back on a timer.
// Sold out is still 409 from Event, after a tap this filter let through.
//
// The key is the JWT subject when the phone sent a token, otherwise the IP.
// Click id is not this key. Click id makes one tap one ticket. This gate stops overflow.
// Compose points the jar at Redis so two gateway pods share one count.
@Component
public class BookRateLimitFilter extends OncePerRequestFilter {

    private final TokenBucket bucket;

    BookRateLimitFilter(TokenBucket bucket) {
        this.bucket = bucket;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if (isBookPost(request)) {
            boolean allowed;
            try {
                allowed = bucket.tryConsume(callerKey(request));
            } catch (RuntimeException ex) {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                return;
            }
            if (!allowed) {
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

    private String callerKey(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String subject = subjectOf(header.substring(7).trim());
            if (subject != null && !subject.isBlank()) {
                return "user:" + subject;
            }
        }
        return "ip:" + request.getRemoteAddr();
    }

    private String subjectOf(String jwt) {
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        try {
            byte[] json = Base64.getUrlDecoder().decode(parts[1]);
            String body = new String(json, StandardCharsets.UTF_8);
            int key = body.indexOf("\"sub\"");
            if (key < 0) {
                return null;
            }
            int colon = body.indexOf(':', key);
            int first = body.indexOf('"', colon + 1);
            int second = body.indexOf('"', first + 1);
            if (first < 0 || second < 0) {
                return null;
            }
            return body.substring(first + 1, second);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
