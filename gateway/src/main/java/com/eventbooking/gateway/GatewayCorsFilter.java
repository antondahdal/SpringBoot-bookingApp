package com.eventbooking.gateway;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

// The browser talks only to the gateway. A 429 from the gate must carry the
// same CORS headers as a proxied call, or the page hides the status.
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayCorsFilter extends OncePerRequestFilter {

    private final List<String> origins;

    GatewayCorsFilter(@Value("${app.cors.allowed-origins:http://localhost:3000}") String origins) {
        this.origins = Arrays.stream(origins.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String origin = request.getHeader("Origin");
        if (origin != null && origins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Correlation-Id");
            response.setHeader("Access-Control-Expose-Headers", "X-Correlation-Id");
        }
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) && origin != null && origins.contains(origin)) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        chain.doFilter(request, new StripDownstreamCors(response));
    }

    // Auth, Event, and Booking also write CORS. The proxy would forward those
    // headers next to the ones this filter already set, and the browser then
    // hides the response.
    private static final class StripDownstreamCors extends HttpServletResponseWrapper {

        StripDownstreamCors(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setHeader(String name, String value) {
            if (!isCors(name)) {
                super.setHeader(name, value);
            }
        }

        @Override
        public void addHeader(String name, String value) {
            if (!isCors(name)) {
                super.addHeader(name, value);
            }
        }

        private static boolean isCors(String name) {
            return name != null && name.regionMatches(true, 0, "Access-Control-", 0, "Access-Control-".length());
        }
    }
}
