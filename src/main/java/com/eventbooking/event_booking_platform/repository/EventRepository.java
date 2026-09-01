package com.eventbooking.event_booking_platform.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import com.eventbooking.event_booking_platform.model.Event;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
public interface  EventRepository extends JpaRepository<Event,Long> {
    

    @Query("SELECT e FROM Event e JOIN FETCH e.venue")
List<Event> findAllWithVenue();

@EntityGraph(attributePaths = "venue")
Page<Event> findAll(Pageable pageable);


 @Query("SELECT e FROM Event e where e.id = :id ")
 @Lock(LockModeType.PESSIMISTIC_WRITE)@QueryHints(  @QueryHint( value = "3000", name = "jakarta.persistence.lock.timeout"))
Optional<Event> findByIdForUpdate(@Param("id") Long id);
}
