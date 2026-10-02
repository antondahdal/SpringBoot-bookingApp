package com.eventbooking.event_booking_platform.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.eventbooking.event_booking_platform.model.HoldStatus;
import com.eventbooking.event_booking_platform.model.SeatHold;

import jakarta.persistence.LockModeType;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {

    @Query ("SELECT s FROM SeatHold s where s.id = :id ")
    @Lock (LockModeType.PESSIMISTIC_WRITE)
    Optional<SeatHold> findByIdForUpdate(Long id);

    List<SeatHold> findByStatusAndExpiresAtBefore (HoldStatus status, LocalDateTime expireyDate); 

}
