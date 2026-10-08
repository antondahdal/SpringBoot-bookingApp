package com.eventbooking.events.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name="seat_holds")
@Getter 
@Setter 
@NoArgsConstructor 
public class SeatHold {
    @Id 
    @GeneratedValue (strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "event_id", nullable = false)
    private Event event;
    @Column(nullable = false)
    private int seats;
    @Column(nullable = false)
    @Enumerated (EnumType.STRING)
    private HoldStatus status;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
