package com.entain.sporteventsapi.service;

import com.entain.sporteventsapi.dto.EventDTO;
import com.entain.sporteventsapi.entity.Event;
import com.entain.sporteventsapi.entity.EventStatus;
import com.entain.sporteventsapi.entity.Sport;
import com.entain.sporteventsapi.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.entain.sporteventsapi.entity.EventStatus.*;
import static com.entain.sporteventsapi.entity.Sport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private Event event;

    @BeforeEach
    public void setUp() {
        event = new Event();
        event.setId(1L);
        event.setName("Test Event");
        event.setSport(FOOTBALL);
        event.setEventStatus(INACTIVE);
        event.setStartTime(LocalDateTime.now().plusDays(1));
    }

    @Test
    public void shouldCreateEvent() {
        EventDTO eventDTO = new EventDTO("Test Event", "FOOTBALL", "INACTIVE", LocalDateTime.now().plusDays(1));
        when(eventRepository.save(any(Event.class))).thenReturn(eventDTO.convertToEntity());

        EventDTO createdEvent = eventService.createEvent(eventDTO);

        assertNotNull(createdEvent);
        assertEquals(event.getName(), createdEvent.getName());
        assertEquals(event.getEventStatus(), EventStatus.valueOf(createdEvent.getEventStatus()));
        assertEquals(event.getSport(), Sport.valueOf(createdEvent.getSport()));
        assertDateTime(event.getStartTime(), createdEvent.getStartTime());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void shouldGetEventById() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventDTO foundEvent = eventService.getEventById(1L);

        assertEquals(event.getName(), foundEvent.getName());
        assertEquals(event.getEventStatus(), EventStatus.valueOf(foundEvent.getEventStatus()));
        assertEquals(event.getSport(), Sport.valueOf(foundEvent.getSport()));
        assertDateTime(event.getStartTime(), foundEvent.getStartTime());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    public void shouldGetEventBySportAndStatus() {
        event = new Event();
        event.setId(2L);
        event.setName("Test boxing Event");
        event.setSport(BOXING);
        event.setEventStatus(ACTIVE);
        event.setStartTime(LocalDateTime.now().plusDays(1));

        when(eventRepository.findByEventStatusAndSport(ACTIVE, BOXING)).thenReturn(List.of(event));

        List<EventDTO> result = eventService.getEvents(ACTIVE.name(), BOXING.name());
        EventDTO expectedEvent = result.get(0);
        assertEquals(1, result.size());
        assertEquals(event.getName(), expectedEvent.getName());
        assertEquals(event.getEventStatus(), EventStatus.valueOf(expectedEvent.getEventStatus()));
        assertEquals(event.getSport(), Sport.valueOf(expectedEvent.getSport()));
        assertDateTime(event.getStartTime(), expectedEvent.getStartTime());
        verify(eventRepository, times(1)).findByEventStatusAndSport(ACTIVE, BOXING);
    }

    @Test
    public void shouldGetByStatus() {
        when(eventRepository.findByEventStatus(INACTIVE)).thenReturn(List.of(event));

        List<EventDTO> result = eventService.getEvents(INACTIVE.name(), null);
        EventDTO expectedEvent = result.get(0);
        assertEquals(1, result.size());
        assertEquals(event.getName(), expectedEvent.getName());
        assertEquals(event.getEventStatus(), EventStatus.valueOf(expectedEvent.getEventStatus()));
        assertEquals(event.getSport(), Sport.valueOf(expectedEvent.getSport()));
        assertDateTime(event.getStartTime(), expectedEvent.getStartTime());
        verify(eventRepository, times(1)).findByEventStatus(INACTIVE);
    }

    @Test
    public void shouldGetBySport() {
        when(eventRepository.findBySport(HOCKEY)).thenReturn(List.of(event));

        List<EventDTO> result = eventService.getEvents(null, HOCKEY.name());
        EventDTO expectedEvent = result.get(0);
        assertEquals(1, result.size());
        assertEquals(event.getName(), expectedEvent.getName());
        assertEquals(event.getEventStatus(), EventStatus.valueOf(expectedEvent.getEventStatus()));
        assertEquals(event.getSport(), Sport.valueOf(expectedEvent.getSport()));
        assertDateTime(event.getStartTime(), expectedEvent.getStartTime());
        verify(eventRepository, times(1)).findBySport(HOCKEY);
    }

    @Test
    public void shouldGetAllEventsIfNoStatusOrSportProvided() {
        Event secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setName("Test boxing Event");
        secondEvent.setSport(BOXING);
        secondEvent.setEventStatus(ACTIVE);
        secondEvent.setStartTime(LocalDateTime.now().plusDays(5));
        when(eventRepository.findAll()).thenReturn(List.of(event, secondEvent));

        List<EventDTO> result = eventService.getEvents(null, null);
        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void shouldUpdateEventStatus() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDTO updatedEvent = eventService.updateEventStatus(1L, "ACTIVE");

        assertNotNull(updatedEvent);
        assertEquals(ACTIVE, EventStatus.valueOf(updatedEvent.getEventStatus()));
        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void shouldThrowWhenIncorrectStatusUpdate() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        event.setEventStatus(FINISHED);

        assertThrows(EventException.class, () -> eventService.updateEventStatus(1L, "INACTIVE"));
        assertThrows(EventException.class, () -> eventService.updateEventStatus(1L, "ACTIVE"));

        verify(eventRepository, times(2)).findById(1L);
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    public void shouldThrowWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.updateEventStatus(1L, "ACTIVE"));

        verify(eventRepository, times(1)).findById(1L);
        verify(eventRepository, never()).save(any(Event.class));
    }

    private void assertDateTime(LocalDateTime expected, LocalDateTime actual) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String expectedFormattedTime = expected.format(formatter);
        String actualFormattedTime = actual.format(formatter);
        assertEquals(expectedFormattedTime, actualFormattedTime);
    }
}
