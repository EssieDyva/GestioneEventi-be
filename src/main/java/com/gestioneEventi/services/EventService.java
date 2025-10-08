package com.gestioneEventi.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioneEventi.dto.event.CreateEventRequest;
import com.gestioneEventi.dto.event.UpdateEventRequest;
import com.gestioneEventi.exceptions.ResourceNotFoundException;
import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.EventType;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.EventRepository;
import com.gestioneEventi.repositories.UserRepository;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Event> getUserEvents(Long id) {
        return eventRepository.findAllByUserId(id);
    }

    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }

    @Transactional
    public Event createEvent(CreateEventRequest request, User creator) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setCreatedBy(creator);

        if (request.getEventType() == null) event.setEventType(EventType.FERIE);
        else event.setEventType(request.getEventType());

        validateEventDates(event);
        
        if (request.getInvitedUserIds() != null && !request.getInvitedUserIds().isEmpty()) {
            Set<User> users = request.getInvitedUserIds().stream()
                    .map(id -> userRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Gruppo", id)))
                    .collect(Collectors.toSet());
            event.setInvitedUsers(users);
        }

        return eventRepository.save(event);
    }

    @Transactional
    public Optional<Event> updateEvent(Long id, UpdateEventRequest request) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(request.getTitle());
            event.setStartDate(request.getStartDate());
            event.setEndDate(request.getEndDate());

            validateEventDates(event);

            if (request.getInvitedUserIds() != null) {
                Set<User> users = request.getInvitedUserIds().stream()
                        .map(groupId -> userRepository.findById(groupId)
                                .orElseThrow(() -> new ResourceNotFoundException("Gruppo", groupId)))
                        .collect(Collectors.toSet());
                event.setInvitedUsers(users);
            }
            return eventRepository.save(event);
        });
    }

    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento", id);
        }
        eventRepository.deleteById(id);
    }

    private void validateEventDates(Event event) {
        if (event.getStartDate() == null || event.getEndDate() == null) {
            throw new IllegalArgumentException("Date di inizio e fine sono obbligatorie");
        }
        if (event.getStartDate().isAfter(event.getEndDate())) {
            throw new IllegalArgumentException("La data di inizio deve essere precedente o uguale alla data di fine");
        }
        if (event.getStartDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La data di inizio non può essere nel passato");
        }
    }
}