package com.gestioneEventi.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioneEventi.dto.partecipation.CreatePartecipation;
import com.gestioneEventi.dto.partecipation.UpdatePartecipation;
import com.gestioneEventi.exceptions.BusinessValidationException;
import com.gestioneEventi.exceptions.ResourceNotFoundException;
import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.Partecipation;
import com.gestioneEventi.models.PartecipationStatus;
import com.gestioneEventi.models.Role;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.EventRepository;
import com.gestioneEventi.repositories.PartecipationRepository;
import com.gestioneEventi.repositories.UserRepository;

/**
 * Service class for managing user participations in events.
 * Handles creation, updates, and queries for participation records with proper authorization checks.
 * Supports bulk operations for creating multiple participations at once.
 *
 */
@Service
public class PartecipationService {

    @Autowired
    private PartecipationRepository partecipationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Creates multiple participation records for users in an event.
     * Validates that all users exist and skips duplicates. All new participations start with PENDING status.
     *
     * @param request The creation request containing event ID and list of user IDs
     * @return List of created participation records
     * @throws BusinessValidationException if event or users are not specified, or if users don't exist
     * @throws ResourceNotFoundException if the event is not found
     */
    @Transactional
    public List<Partecipation> createPartecipation(CreatePartecipation request) {
        if (request.getEventId() == null)
            throw new BusinessValidationException("Evento non specificato");
        List<Long> requestedIds = Optional.ofNullable(request.getUserIds()).orElse(List.of());
        if (requestedIds.isEmpty())
            throw new BusinessValidationException("Nessun utente specificato");

        List<Long> uniqueIds = requestedIds.stream().distinct().toList();
        List<User> foundUsers = userRepository.findAllById(uniqueIds);
        Set<Long> foundIds = foundUsers.stream().map(User::getId).collect(Collectors.toSet());
        List<Long> missing = uniqueIds.stream().filter(id -> !foundIds.contains(id)).toList();
        if (!missing.isEmpty()) {
            throw new BusinessValidationException("Utenti non trovati: " + missing);
        }

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento", request.getEventId()));

        List<Partecipation> existing = partecipationRepository.findByEventIdAndUserIdIn(event.getId(), uniqueIds);
        Set<Long> alreadyJoinedUserIds = existing.stream().map(p -> p.getUser().getId()).collect(Collectors.toSet());

        List<Partecipation> toCreate = foundUsers.stream()
                .filter(u -> !alreadyJoinedUserIds.contains(u.getId()))
                .map(user -> {
                    Partecipation p = new Partecipation();
                    p.setStatus(PartecipationStatus.PENDING);
                    p.setEvent(event);
                    p.setUser(user);
                    return p;
                })
                .toList();

        return partecipationRepository.saveAll(toCreate);
    }

    /**
     * Convenience method to create participations using event ID and user IDs directly.
     *
     * @param eventId The ID of the event
     * @param userIds List of user IDs to create participations for
     * @return List of created participation records
     */
    @Transactional
    public List<Partecipation> createPartecipation(Long eventId, List<Long> userIds) {
        CreatePartecipation request = new CreatePartecipation();
        request.setEventId(eventId);
        request.setUserIds(userIds);
        return createPartecipation(request);
    }

    /**
     * Updates the status of a participation record.
     * Only the participation owner or privileged users (ADMIN/EDITOR) can update the status.
     *
     * @param request The update request containing new status
     * @param id The ID of the participation to update
     * @param currentUser The user attempting the update
     * @return The updated participation record
     * @throws ResourceNotFoundException if the participation is not found
     * @throws BusinessValidationException if the user lacks permission to update
     */
    @Transactional
    public Partecipation updatePartecipation(UpdatePartecipation request, Long id, User currentUser) {
        Partecipation partecipation = partecipationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partecipazione", id));

        boolean isOwner = partecipation.getUser().getId().equals(currentUser.getId());
        boolean isPrivileged = currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.EDITOR;

        if (!isOwner && !isPrivileged) {
            throw new BusinessValidationException("Non puoi modificare la partecipazione");
        }

        partecipation.setStatus(request.getStatus());
        return partecipationRepository.save(partecipation);
    }

    /**
     * Retrieves all participation records in the system.
     *
     * @return List of all participations
     */
    public List<Partecipation> getAllPartecipations() {
        return partecipationRepository.findAll();
    }

    /**
     * Retrieves all participations for a specific event.
     *
     * @param eventId The ID of the event
     * @return List of participations for the specified event
     */
    public List<Partecipation> getPartecipationsByEventId(Long eventId) {
        return partecipationRepository.findByEventId(eventId);
    }

    /**
     * Retrieves all participations for a specific user.
     *
     * @param userId The ID of the user
     * @return List of participations for the specified user
     */
    public List<Partecipation> getPartecipationsByUserId(Long userId) {
        return partecipationRepository.findByUserId(userId);
    }

    /**
     * Gets the owner (user) ID of a participation record.
     *
     * @param partecipationId The ID of the participation
     * @return The user ID of the participation owner, or null if not found
     */
    public Long getOwnerId(Long partecipationId) {
        return partecipationRepository.findById(partecipationId)
                .map(p -> p.getUser().getId())
                .orElse(null);
    }

    /**
     * Deletes a participation record by ID.
     *
     * @param id The ID of the participation to delete
     * @throws ResourceNotFoundException if the participation is not found
     */
    @Transactional
    public void deletePartecipation(Long id) {
        if (!partecipationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Partecipazione", id);
        }
        partecipationRepository.deleteById(id);
    }
}
