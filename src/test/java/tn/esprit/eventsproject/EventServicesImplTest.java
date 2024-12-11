package tn.esprit.eventsproject;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;
import tn.esprit.eventsproject.services.EventServicesImpl;

import java.time.LocalDate;
import java.util.*;

class EventServicesImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @InjectMocks
    private EventServicesImpl eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddParticipant() {
        Participant participant = new Participant();
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);
        Participant result = eventService.addParticipant(participant);
        assertEquals(participant, result);
        verify(participantRepository).save(participant);
    }

    @Test
    void testAddAffectEvenParticipantWithId() {
        Event event = new Event();
        Participant participant = new Participant();
        participant.setIdPart(1);
        Set<Event> events = new HashSet<>();
        events.add(event);
        participant.setEvents(events);

        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.addAffectEvenParticipant(event, 1);
        assertNotNull(result);
        assertTrue(participant.getEvents().contains(event));
        verify(participantRepository).findById(1);
        verify(eventRepository).save(event);
    }

    @Test
    void testAddAffectEvenParticipantWithoutId() {
        Event event = new Event();
        Participant participant = new Participant();
        participant.setIdPart(1);
        Set<Event> events = new HashSet<>();
        events.add(event);
        Set<Participant> participants = new HashSet<>();
        participants.add(participant);

        event.setParticipants(participants);

        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        Event result = eventService.addAffectEvenParticipant(event);
        assertNotNull(result);
        assertTrue(participant.getEvents().contains(event));
        verify(participantRepository, times(1)).findById(1);
        verify(eventRepository).save(event);
    }

    @Test
    void testAddAffectLog() {
        Event event = new Event();
        Logistics logistics = new Logistics();
        String description = "Test Event";

        when(eventRepository.findByDescription(description)).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics result = eventService.addAffectLog(logistics, description);
        assertEquals(logistics, result);
        assertTrue(event.getLogistics().contains(logistics));
        verify(eventRepository).findByDescription(description);
        verify(logisticsRepository).save(logistics);
    }

    @Test
    void testGetLogisticsDates() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);
        Event event = new Event();
        Logistics logistics = new Logistics();
        logistics.setReserve(true);
        Set<Logistics> logisticsSet = new HashSet<>();
        logisticsSet.add(logistics);
        event.setLogistics(logisticsSet);
        List<Event> events = Collections.singletonList(event);

        when(eventRepository.findByDateDebutBetween(startDate, endDate)).thenReturn(events);

        List<Logistics> result = eventService.getLogisticsDates(startDate, endDate);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains(logistics));
        verify(eventRepository).findByDateDebutBetween(startDate, endDate);
    }

    @Test
    void testCalculCout() {
        Event event = new Event();
        Logistics logistics = new Logistics();
        logistics.setPrixUnit(100f);
        logistics.setQuantite(2);
        logistics.setReserve(true);
        Set<Logistics> logisticsSet = new HashSet<>();
        logisticsSet.add(logistics);
        event.setLogistics(logisticsSet);
        List<Event> events = Collections.singletonList(event);

        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR)).thenReturn(events);

        eventService.calculCout();
        verify(eventRepository).findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR);
        verify(eventRepository).save(event);
        assertEquals(200f, event.getCout(), 0.001);
    }
}
