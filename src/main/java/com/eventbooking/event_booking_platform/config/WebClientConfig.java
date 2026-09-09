package com.eventbooking.event_booking_platform.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
    
    @Bean
    public WebClient getWebClient(@Value("${event.service.base-url}") String url){
        Duration dur=Duration.ofSeconds(3);
        HttpClient plugin=HttpClient.create().responseTimeout(dur);
        ReactorClientHttpConnector plug=new ReactorClientHttpConnector(plugin);
        return WebClient.builder().baseUrl(url).clientConnector(plug).build();
    }
}
