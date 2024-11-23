package com.entain.sporteventsapi.dto;

import com.entain.sporteventsapi.entity.Event;
import com.entain.sporteventsapi.entity.EventStatus;
import com.entain.sporteventsapi.entity.Sport;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EventDTO {
    private final Long id;
    private final String name;
    private final String sport;
    @JsonProperty("status")
    private final String eventStatus;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final LocalDateTime startTime;

    public EventDTO(String name, String sport, String eventStatus, LocalDateTime startTime) {
        this.id = null;
        this.name = name;
        this.sport = sport;
        this.eventStatus = eventStatus;
        this.startTime = startTime;
    }

    public EventDTO(Event entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.sport = entity.getSport().name();
        this.eventStatus = entity.getEventStatus().name();
        this.startTime = entity.getStartTime();

    }

    public Event convertToEntity() {
        Event event = new Event();
        event.setId(null);
        event.setName(this.name);
        event.setSport(Sport.valueOf(this.sport));
        event.setEventStatus(EventStatus.valueOf(this.eventStatus));
        event.setStartTime(this.startTime);
        return event;
    }
}
