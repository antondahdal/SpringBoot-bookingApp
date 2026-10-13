package com.eventbooking.gateway;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// The browser sends OPTIONS before every call because of X-Correlation-Id.
// The gateway answered that itself, with no CORS headers, so the page reported the gateway as down.
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayPreflightFilter extends OncePerRequestFilter {

    static final String ORIGIN = "http://localhost:3000";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) && ORIGIN.equals(request.getHeader("Origin"))) {
            response.setHeader("Access-Control-Allow-Origin", ORIGIN);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Correlation-Id");
            response.setHeader("Access-Control-Expose-Headers", "X-Correlation-Id");
            response.setHeader("Vary", "Origin");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        chain.doFilter(request, response);
    }
}
