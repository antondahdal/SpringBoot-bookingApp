package com.eventbooking.event_booking_platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync 
@SpringBootApplication
@EnableScheduling 
public class EventBookingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventBookingPlatformApplication.class, args);
	}

}
