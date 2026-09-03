package com.eventbooking.event_booking_platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient getWebClient(@Value("${event.service.base-url}") String url){

        return WebClient.builder().baseUrl(url).build();
    }
}
