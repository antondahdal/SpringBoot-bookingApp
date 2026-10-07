package com.eventbooking.event_booking_platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventbooking.event_booking_platform.model.Booking;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    List<Booking> findByUserId(Long userId);
}
