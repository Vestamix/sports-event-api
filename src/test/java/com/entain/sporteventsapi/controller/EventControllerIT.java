package com.entain.sporteventsapi.controller;

import com.entain.sporteventsapi.dto.EventDTO;
import com.entain.sporteventsapi.entity.EventStatus;
import com.entain.sporteventsapi.entity.Sport;
import com.entain.sporteventsapi.repository.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static com.entain.sporteventsapi.entity.EventStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class EventControllerIT {
    private static final String URL = "/api/events";
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventRepository eventRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @AfterEach
    public void cleanUp() {
        eventRepository.deleteAll();
    }

    @Test
    void shouldCreateEvent() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2024, 10, 4, 13, 30);
        EventDTO eventDTO = getEvent("Hockey event", Sport.HOCKEY, ACTIVE, dateTime);
        String expectedJson = objectMapper.writeValueAsString(eventDTO);

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJson))
                .andExpect(status().isOk())
                .andReturn();

        EventDTO createdEvent = objectMapper.readValue(result.getResponse().getContentAsString(), EventDTO.class);
        assertNotNull(createdEvent);
        assertEquals(eventDTO.getName(), createdEvent.getName());
        assertEquals(eventDTO.getSport(), createdEvent.getSport());
        assertEquals(eventDTO.getEventStatus(), createdEvent.getEventStatus());
        assertEquals(eventDTO.getStartTime(), createdEvent.getStartTime());
    }

    @Test
    void shouldGetEventsBySport() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2024, 10, 4, 13, 30);

        EventDTO boxingEventDto = getEvent("Test boxing", Sport.BOXING, ACTIVE, dateTime);
        EventDTO footballEventDto = getEvent("Football test", Sport.FOOTBALL, FINISHED, dateTime.minusDays(5));
        eventRepository.saveAll(List.of(boxingEventDto.convertToEntity(), footballEventDto.convertToEntity()));

        MvcResult result = mockMvc.perform(get(URL)
                        .param("sport", "FOOTBALL"))
                .andExpect(status().isOk())
                .andReturn();

        List<EventDTO> events = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.stream().allMatch(event -> event.getSport().equals(Sport.FOOTBALL.name())));
    }

    @Test
    void shouldGetEventsByStatus() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2024, 10, 4, 13, 30);

        EventDTO boxingEventDto = getEvent("Test boxing", Sport.BOXING, ACTIVE, dateTime);
        EventDTO footballEventDto = getEvent("Football test", Sport.FOOTBALL, FINISHED, dateTime.minusDays(5));
        eventRepository.saveAll(List.of(boxingEventDto.convertToEntity(), footballEventDto.convertToEntity()));

        MvcResult result = mockMvc.perform(get(URL)
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andReturn();

        List<EventDTO> events = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.stream().allMatch(event -> event.getEventStatus().equals(ACTIVE.name())));
    }

    @Test
    void shouldGetEventsByStatusAndSport() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2024, 10, 4, 13, 30);

        EventDTO boxingEventDto = getEvent("Test boxing", Sport.BOXING, ACTIVE, dateTime);
        EventDTO hockeyEventDto = getEvent("Hockey test", Sport.HOCKEY, ACTIVE, dateTime.plusHours(1));
        eventRepository.saveAll(List.of(boxingEventDto.convertToEntity(), hockeyEventDto.convertToEntity()));

        MvcResult result = mockMvc.perform(get(URL)
                        .param("sport", "BOXING")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andReturn();

        List<EventDTO> events = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(events);
        assertEquals(1, events.size());
        assertTrue(events.stream().allMatch(event -> event.getEventStatus().equals(ACTIVE.name()) && event.getSport().equals(Sport.BOXING.name())));
    }

    @Test
    void shouldGetAllEventsIfNoStatusAndSportProvided() throws Exception {
        LocalDateTime dateTime = LocalDateTime.of(2024, 10, 4, 13, 30);

        EventDTO boxingEventDto = getEvent("Test boxing", Sport.BOXING, ACTIVE, dateTime);
        EventDTO hockeyEventDto = getEvent("Hockey test", Sport.HOCKEY, ACTIVE, dateTime.plusHours(1));
        eventRepository.saveAll(List.of(boxingEventDto.convertToEntity(), hockeyEventDto.convertToEntity()));

        MvcResult result = mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andReturn();

        List<EventDTO> events = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(events);
        assertEquals(2, events.size());
    }

    @Test
    void getEventById() throws Exception {
        EventDTO createdEvent = createEvent();

        MvcResult result = mockMvc.perform(get(URL + "/" + createdEvent.getId()))
                .andExpect(status().isOk())
                .andReturn();
        EventDTO resultEvent = objectMapper.readValue(result.getResponse().getContentAsString(), EventDTO.class);

        assertNotNull(resultEvent);
        assertEquals(createdEvent.getId(), resultEvent.getId());
    }

    @Test
    void shouldUpdateEventStatus() throws Exception {
        EventDTO createdEvent = createEvent();

        MvcResult result = mockMvc.perform(get(URL + "/" + createdEvent.getId()))
                .andExpect(status().isOk())
                .andReturn();
        EventDTO resultEvent = objectMapper.readValue(result.getResponse().getContentAsString(), EventDTO.class);
        assertNotNull(resultEvent);
        assertEquals(createdEvent.getEventStatus(), resultEvent.getEventStatus());

        MvcResult updatedResult = mockMvc.perform(put(URL + "/" + createdEvent.getId() + "/status")
                        .param("newStatus", FINISHED.name()))
                .andExpect(status().isOk())
                .andReturn();
        EventDTO updatedEvent = objectMapper.readValue(updatedResult.getResponse().getContentAsString(), EventDTO.class);
        assertNotNull(updatedEvent);
        assertEquals(FINISHED.name(), updatedEvent.getEventStatus());
    }

    @Test
    void shouldNotChangeFinishedStatus() throws Exception {
        EventDTO finishedEvent = createEvent(FINISHED);

        mockMvc.perform(put(URL + "/" + finishedEvent.getId() + "/status")
                        .param("newStatus", INACTIVE.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowIfNoEventFound() throws Exception {
        mockMvc.perform(get(URL + "/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldNotChangeFromInactiveToFinished() throws Exception {
        EventDTO inactiveEvent = createEvent(INACTIVE);

        mockMvc.perform(put(URL + "/" + inactiveEvent.getId() + "/status")
                        .param("newStatus", FINISHED.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldNotActivateEventIfStartTimePassed() throws Exception {
        EventDTO inactiveEvent = createEvent(INACTIVE, LocalDateTime.now().minusDays(20));

        mockMvc.perform(put(URL + "/" + inactiveEvent.getId() + "/status")
                        .param("newStatus", ACTIVE.name()))
                .andExpect(status().isBadRequest());
    }

    private EventDTO createEvent() throws Exception {
        return createEvent(ACTIVE, LocalDateTime.now());
    }

    private EventDTO createEvent(EventStatus eventStatus) throws Exception {
        return createEvent(eventStatus, LocalDateTime.now());
    }

    private EventDTO createEvent(EventStatus eventStatus, LocalDateTime startTime) throws Exception {
        EventDTO eventDTO = getEvent("Hockey event", Sport.HOCKEY, eventStatus, startTime);
        String expectedJson = objectMapper.writeValueAsString(eventDTO);

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJson))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), EventDTO.class);
    }

    private EventDTO getEvent(String name, Sport sport, EventStatus status, LocalDateTime dateTime) {
        return new EventDTO(name, sport.name(), status.name(), dateTime);
    }
}