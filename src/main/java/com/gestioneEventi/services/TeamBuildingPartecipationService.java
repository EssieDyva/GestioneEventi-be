package com.gestioneEventi.services;

import com.gestioneEventi.dto.teambuilding.CreateTeamBuildingPartecipationRequest;
import com.gestioneEventi.exceptions.ResourceNotFoundException;
import com.gestioneEventi.models.*;
import com.gestioneEventi.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TeamBuildingPartecipationService {

    @Autowired
    private TeamBuildingPartecipationRepository partecipationRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private ActivityRepository activityRepository;

    @Transactional
    public TeamBuildingPartecipation createPartecipation(Long eventId, CreateTeamBuildingPartecipationRequest request, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));

        if (event.getEventType() != EventType.TEAM_BUILDING) {
            throw new IllegalArgumentException("Le partecipazioni con attività sono valide solo per eventi TEAM_BUILDING.");
        }

        List<TeamBuildingPartecipation> existingParticipations = partecipationRepository.findByUserAndEvent(user, event);
        Set<Long> existingActivityIds = existingParticipations.stream()
                .flatMap(p -> p.getChosenActivities().stream())
                .map(Activity::getId)
                .collect(java.util.stream.Collectors.toSet());

        if (!existingActivityIds.isEmpty()) {
            Set<Long> requestedActivityIds = request.getActivityIds() != null ?
                    new HashSet<>(request.getActivityIds()) : new HashSet<>();
            if (!existingActivityIds.equals(requestedActivityIds)) {
                throw new IllegalArgumentException("Puoi scegliere solo le stesse attività già selezionate per questo evento.");
            }
        }

        TeamBuildingPartecipation partecipation = new TeamBuildingPartecipation();
        partecipation.setEvent(event);
        partecipation.setUser(user);

        if (request.getActivityIds() == null || request.getActivityIds().isEmpty()) {
            throw new IllegalArgumentException("Devi selezionare un'attività.");
        }
        if (request.getActivityIds().size() != 1) {
            throw new IllegalArgumentException("Puoi selezionare solo un'attività.");
        }

        Set<Activity> activities = new HashSet<>();
        for (Long id : request.getActivityIds()) {
            Activity act = activityRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Attività", id));
            if (!act.getEvent().equals(event)) {
                throw new IllegalArgumentException("L'attività " + act.getId() + " non appartiene a questo evento.");
            }
            activities.add(act);
        }
        partecipation.setChosenActivities(activities);

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new IllegalArgumentException("Date di inizio e fine sono obbligatorie.");
        }
        if (request.getStartDate().isBefore(event.getStartDate()) || request.getStartDate().isAfter(event.getEndDate()) ||
            request.getEndDate().isBefore(event.getStartDate()) || request.getEndDate().isAfter(event.getEndDate()) ||
            request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Le date selezionate sono invalide o fuori dal periodo dell'evento.");
        }

        partecipation.setStartDate(request.getStartDate());
        partecipation.setEndDate(request.getEndDate());

        return partecipationRepository.save(partecipation);
    }

    @Transactional(readOnly = true)
    public List<TeamBuildingPartecipation> getPartecipationsForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));

        return partecipationRepository.findByEvent(event);
    }

    @Transactional(readOnly = true)
    public java.util.Map<Long, Long> getActivityPopularity(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));

        List<TeamBuildingPartecipation> partecipations = partecipationRepository.findByEvent(event);

        Set<String> uniqueUserActivityPairs = new HashSet<>();
        for (TeamBuildingPartecipation p : partecipations) {
            Long userId = p.getUser().getId();
            for (Activity activity : p.getChosenActivities()) {
                uniqueUserActivityPairs.add(userId + "-" + activity.getId());
            }
        }

        Map<Long, Long> popularity = new HashMap<>();
        for (String pair : uniqueUserActivityPairs) {
            Long activityId = Long.parseLong(pair.split("-")[1]);
            popularity.put(activityId, popularity.getOrDefault(activityId, 0L) + 1);
        }

        return popularity;
    }

    @Transactional
    public void deletePartecipation(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));

        List<TeamBuildingPartecipation> participations = partecipationRepository.findByUserAndEvent(user, event);
        if (participations.isEmpty()) {
            throw new IllegalArgumentException("Non hai partecipazioni registrate per questo evento.");
        }

        partecipationRepository.deleteAll(participations);
    }
}