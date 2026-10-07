package com.eventbooking.event_booking_platform.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "bookings" ,indexes = { @Index(name = "idx_booking_user", columnList = "user_id") })
@Getter
@Setter
 @NoArgsConstructor
public class Booking {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
   
    private Long id;
    @Column(nullable = false)
    private Long eventId;
    @Column(nullable = false)
     private Long userId;
    @Column(nullable = false)
    private int seats;
    @Column(nullable = false)
    @Enumerated (EnumType.STRING)
    BookingStatus status;    
    @Column(nullable = false)
    private String eventTitle;
}
