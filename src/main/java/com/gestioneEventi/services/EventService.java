package com.gestioneEventi.services;

import java.time.LocalDate;
import java.util.HashSet;
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
import com.gestioneEventi.repositories.ActivityRepository;
import com.gestioneEventi.repositories.EventRepository;
import com.gestioneEventi.repositories.FerieRepository;
import com.gestioneEventi.repositories.PartecipationRepository;
import com.gestioneEventi.repositories.TeamBuildingPartecipationRepository;
import com.gestioneEventi.repositories.UserRepository;

/**
 * Service class for managing event-related operations.
 * Handles CRUD operations for events, including different event types (generic, vacation, team building).
 * Manages event participants, validation, and cascading deletions.
 *
 */
@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FerieRepository ferieRepository;

    @Autowired
    private PartecipationRepository partecipationRepository;

    @Autowired
    private PartecipationService partecipationService;

    @Autowired
    private TeamBuildingPartecipationRepository teamBuildingPartecipationRepository;

    @Autowired
    private ActivityRepository activityRepository;

    /**
     * Retrieves all events from the database.
     *
     * @return List of all events
     */
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Retrieves all events created by a specific user.
     *
     * @param id The ID of the user whose events to retrieve
     * @return List of events created by the specified user
     */
    @Transactional(readOnly = true)
    public List<Event> getUserEvents(Long id) {
        return eventRepository.findAllByUserId(id);
    }

    /**
     * Retrieves a specific event by its ID.
     *
     * @param id The ID of the event to retrieve
     * @return The event with the specified ID
     * @throws ResourceNotFoundException if the event is not found
     */
    @Transactional(readOnly = true)
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));
    }

    /**
     * Creates a new event based on the provided request.
     * Sets default event type if not specified, validates dates, and creates participations for generic events.
     *
     * @param request The event creation request containing event details
     * @param creator The user creating the event
     * @return The created and saved event
     * @throws IllegalArgumentException if date validation fails
     * @throws ResourceNotFoundException if any invited user is not found
     */
    @Transactional
    public Event createEvent(CreateEventRequest request, User creator) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setCreatedBy(creator);

        if (request.getEventType() == null) {
            event.setEventType(EventType.FERIE);
        } else {
            event.setEventType(request.getEventType());
        }

        validateEventDates(event);

        if (request.getInvitedUserIds() != null && !request.getInvitedUserIds().isEmpty()) {
            Set<User> users = request.getInvitedUserIds().stream()
                    .map(id -> userRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Utente", id)))
                    .collect(Collectors.toSet());
            event.setInvitedUsers(users);
        }

        Event savedEvent = eventRepository.save(event);

        if (savedEvent.getEventType() == EventType.GENERICO) {
            partecipationService.createPartecipation(savedEvent.getId(),
                    savedEvent.getInvitedUsers().stream().map(User::getId).toList());
        }

        return savedEvent;
    }

    /**
     * Updates an existing event with new information.
     * Validates dates and manages participant changes for generic events.
     *
     * @param id The ID of the event to update
     * @param request The update request containing new event details
     * @return Optional containing the updated event, or empty if event not found
     * @throws IllegalArgumentException if date validation fails
     * @throws ResourceNotFoundException if any invited user is not found
     */
    @Transactional
    public Optional<Event> updateEvent(Long id, UpdateEventRequest request) {
        return eventRepository.findById(id).map(event -> {
            event.setTitle(request.getTitle());
            event.setStartDate(request.getStartDate());
            event.setEndDate(request.getEndDate());
            validateEventDates(event);

            // Handle confirmed fields only for TEAM_BUILDING events
            if (event.getEventType() == EventType.TEAM_BUILDING) {
                event.setConfirmedStartDate(request.getConfirmedStartDate());
                event.setConfirmedEndDate(request.getConfirmedEndDate());
                event.setConfirmedActivityId(request.getConfirmedActivityId());
                validateConfirmedFields(event);
            } else if (request.getConfirmedStartDate() != null || request.getConfirmedEndDate() != null || request.getConfirmedActivityId() != null) {
                throw new IllegalArgumentException("I campi di conferma possono essere impostati solo per eventi di tipo TEAM_BUILDING");
            }

            Set<User> oldInvited = new HashSet<>(event.getInvitedUsers());

            if (request.getInvitedUserIds() != null) {
                Set<User> newInvited = request.getInvitedUserIds().stream()
                        .map(userId -> userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("Utente", userId)))
                        .collect(Collectors.toSet());
                event.setInvitedUsers(newInvited);

                Set<User> newlyAdded = new HashSet<>(newInvited);
                newlyAdded.removeAll(oldInvited);

                if (!newlyAdded.isEmpty() &&
                        (event.getEventType() == EventType.GENERICO)) {
                    partecipationService.createPartecipation(event.getId(),
                            newlyAdded.stream().map(User::getId).toList());
                }
            }

            return eventRepository.save(event);
        });
    }

    /**
     * Deletes an event and all its associated data based on event type.
     * Performs cascading deletion of related entities (participations, activities, etc.).
     *
     * @param id The ID of the event to delete
     * @throws ResourceNotFoundException if the event is not found
     * @throws IllegalArgumentException if the event type is invalid
     */
    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", id));

        final EventType eventType = event.getEventType();
        switch (eventType) {
            case GENERICO:
                partecipationRepository.deleteAllByEvent(event);
                break;
            case FERIE:
                ferieRepository.deleteAllByEvent(event);
                break;
            case TEAM_BUILDING:
                teamBuildingPartecipationRepository.deleteAllByEvent(event);
                activityRepository.deleteAll(activityRepository.findByEvent(event));
                break;
            default:
                throw new IllegalArgumentException("Tipo di evento non valido: " + eventType);
        }

        eventRepository.delete(event);
    }

    /**
     * Validates the start and end dates of an event.
     * Ensures dates are present, start date is not after end date, and start date is not in the past.
     *
     * @param event The event whose dates to validate
     * @throws IllegalArgumentException if any validation rule is violated
     */
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

    /**
     * Validates the confirmed fields for team building events.
     * Ensures confirmed dates are within event range and confirmed activity exists.
     *
     * @param event The event whose confirmed fields to validate
     * @throws IllegalArgumentException if any validation rule is violated
     */
    private void validateConfirmedFields(Event event) {
        LocalDate confirmedStart = event.getConfirmedStartDate();
        LocalDate confirmedEnd = event.getConfirmedEndDate();
        Long confirmedActivityId = event.getConfirmedActivityId();

        // If any confirmed field is set, all must be set for consistency
        boolean hasConfirmedDates = confirmedStart != null || confirmedEnd != null;
        boolean hasConfirmedActivity = confirmedActivityId != null;

        if (hasConfirmedDates || hasConfirmedActivity) {
            if (confirmedStart == null || confirmedEnd == null || confirmedActivityId == null) {
                throw new IllegalArgumentException("Tutti i campi di conferma (date e attività) devono essere impostati insieme");
            }

            // Validate confirmed dates are within event range
            if (confirmedStart.isBefore(event.getStartDate()) || confirmedStart.isAfter(event.getEndDate())) {
                throw new IllegalArgumentException("La data di inizio confermata deve essere compresa nell'intervallo dell'evento");
            }
            if (confirmedEnd.isBefore(event.getStartDate()) || confirmedEnd.isAfter(event.getEndDate())) {
                throw new IllegalArgumentException("La data di fine confermata deve essere compresa nell'intervallo dell'evento");
            }
            if (confirmedStart.isAfter(confirmedEnd)) {
                throw new IllegalArgumentException("La data di inizio confermata deve essere precedente o uguale alla data di fine confermata");
            }

            // Validate confirmed activity exists for this event
            boolean activityExists = event.getActivities().stream()
                    .anyMatch(activity -> activity.getId().equals(confirmedActivityId));
            if (!activityExists) {
                throw new IllegalArgumentException("L'attività confermata deve essere una delle attività associate all'evento");
            }
        }
    }
}