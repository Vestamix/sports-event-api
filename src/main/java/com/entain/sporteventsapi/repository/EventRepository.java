package com.entain.sporteventsapi.repository;

import com.entain.sporteventsapi.entity.Event;
import com.entain.sporteventsapi.entity.EventStatus;
import com.entain.sporteventsapi.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEventStatusAndSport(EventStatus status, Sport sport);
    List<Event> findByEventStatus(EventStatus status);
    List<Event> findBySport(Sport sport);
}
