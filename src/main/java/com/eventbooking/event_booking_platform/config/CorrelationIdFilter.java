package com.eventbooking.event_booking_platform.config;

import java.io.IOException;
import java.util.UUID;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CorrelationIdFilter extends  OncePerRequestFilter{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
             String header=request.getHeader("X-Correlation-Id");
                if(header==null|| header.isEmpty()) {
                    String id=UUID.randomUUID().toString();
                    request. setAttribute("X-Correlation-Id",id );
                    response.addHeader("X-Correlation-Id", id); 
                }
                else{
                    request. setAttribute("X-Correlation-Id",header);
                    response.addHeader("X-Correlation-Id", header); 
                }
             
                filterChain.doFilter(request, response);
       
    }
    
}
