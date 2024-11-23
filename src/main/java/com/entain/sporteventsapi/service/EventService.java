package com.entain.sporteventsapi.service;

import com.entain.sporteventsapi.dto.EventDTO;
import com.entain.sporteventsapi.entity.Event;
import com.entain.sporteventsapi.entity.EventStatus;
import com.entain.sporteventsapi.entity.Sport;
import com.entain.sporteventsapi.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.entain.sporteventsapi.entity.EventStatus.*;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository repository;

    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = eventDTO.convertToEntity();
        event = repository.save(event);
        return new EventDTO(event);
    }

    public List<EventDTO> getEvents(String status, String sport) {
        List<Event> events;
        if (status != null && sport != null) {
            events = repository.findByEventStatusAndSport(EventStatus.valueOf(status), Sport.valueOf(sport));
        } else if (status != null) {
            events = repository.findByEventStatus(EventStatus.valueOf(status));
        } else if (sport != null) {
            events = repository.findBySport(Sport.valueOf(sport));
        } else {
            events = repository.findAll();
        }
        return events.stream()
                .map(EventDTO::new)
                .toList();
    }

    public EventDTO getEventById(Long id) throws EventNotFoundException {
        return repository.findById(id)
                .map(EventDTO::new)
                .orElseThrow(() -> new EventNotFoundException("Event with id %s not found".formatted(id)));
    }

    public EventDTO updateEventStatus(Long id, String newStatus) throws EventException, EventNotFoundException {
        Event event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event with id %s not found".formatted(id)));
        EventStatus newEventStatus = EventStatus.valueOf(newStatus);
        LocalDateTime dateTimeNow = LocalDateTime.now();

        if (FINISHED == event.getEventStatus()) {
            throw new EventException("Finished event cannot be changed");
        }
        if (INACTIVE == event.getEventStatus() && FINISHED == newEventStatus) {
            throw new EventException("Inactive event cannot be changed to finished");
        }
        if (INACTIVE == event.getEventStatus() && ACTIVE == newEventStatus && event.getStartTime().isBefore(dateTimeNow)) {
            throw new EventException("Cannot activate an event if start time is in the past");
        }
        event.setEventStatus(newEventStatus);
        Event updatedEvent = repository.save(event);
        return new EventDTO(updatedEvent);
    }
}
