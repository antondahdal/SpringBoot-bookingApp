package com.eventbooking.event_booking_platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eventbooking.event_booking_platform.model.OutboxMessage;

/**
 * OutboxmessageRepository
 */
public interface OutboxmessageRepository extends JpaRepository<OutboxMessage,Long> {

    
 
    @Query("SELECT e FROM OutboxMessage e where e.bookingId = :bookingId")
    Optional<OutboxMessage> findByBookingId(Long bookingId);

    List<OutboxMessage> findByStatus(String Status);
    

    
}