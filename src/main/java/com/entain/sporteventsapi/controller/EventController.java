package com.entain.sporteventsapi.controller;

import com.entain.sporteventsapi.dto.EventDTO;
import com.entain.sporteventsapi.entity.EventStatus;
import com.entain.sporteventsapi.service.EventException;
import com.entain.sporteventsapi.service.EventNotFoundException;
import com.entain.sporteventsapi.service.EventService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public EventDTO createEvent(@RequestBody EventDTO eventDTO) {
        return eventService.createEvent(eventDTO);
    }

    @GetMapping
    public List<EventDTO> getEvents(@RequestParam(required = false) String status,
                                    @RequestParam(required = false) String sport) {
        return eventService.getEvents(status, sport);
    }

    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @PutMapping("/{id}/status")
    public EventDTO updateEventStatus(@PathVariable Long id, @RequestParam String newStatus) {
        return eventService.updateEventStatus(id, newStatus);
    }
}
