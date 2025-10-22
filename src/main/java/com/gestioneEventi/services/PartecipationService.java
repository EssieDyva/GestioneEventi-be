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
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.EventRepository;
import com.gestioneEventi.repositories.PartecipationRepository;
import com.gestioneEventi.repositories.UserRepository;

@Service
public class PartecipationService {

    @Autowired
    private PartecipationRepository partecipationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

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
                    p.setIsEventAccepted(false);
                    p.setEvent(event);
                    p.setUser(user);
                    return p;
                })
                .toList();

        return partecipationRepository.saveAll(toCreate);
    }

    @Transactional
    public List<Partecipation> createPartecipation(Long eventId, List<Long> userIds) {
        CreatePartecipation request = new CreatePartecipation();
        request.setEventId(eventId);
        request.setUserIds(userIds);
        return createPartecipation(request);
    }

    @Transactional
    public Partecipation updatePartecipation(UpdatePartecipation request, Long id) {
        Partecipation partecipation = partecipationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partecipazione", id));

        partecipation.setIsEventAccepted(request.getIsEventAccepted());

        return partecipationRepository.save(partecipation);
    }

    public List<Partecipation> getAllPartecipations() {
        return partecipationRepository.findAll();
    }

    public List<Partecipation> getPartecipationsByEventId(Long eventId) {
        return partecipationRepository.findAll().stream()
                .filter(p -> p.getEvent() != null && p.getEvent().getId().equals(eventId))
                .toList();
    }

    public List<Partecipation> getPartecipationsByUserId(Long userId) {
        return partecipationRepository.findAll().stream()
                .filter(p -> p.getUser() != null && p.getUser().getId().equals(userId))
                .toList();
    }

    @Transactional
    public void deletePartecipation(Long id) {
        if (!partecipationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Partecipazione", id);
        }
        partecipationRepository.deleteById(id);
    }
}
