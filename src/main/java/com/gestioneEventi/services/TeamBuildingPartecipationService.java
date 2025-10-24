package com.gestioneEventi.services;

import com.gestioneEventi.dto.teambuilding.CreateTeamBuildingPartecipationRequest;
import com.gestioneEventi.exceptions.ResourceNotFoundException;
import com.gestioneEventi.models.*;
import com.gestioneEventi.repositories.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
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

        partecipationRepository.findByUserAndEvent(user, event)
                .ifPresent(p -> { throw new IllegalArgumentException("Hai già partecipato a questo evento."); });

        TeamBuildingPartecipation partecipation = new TeamBuildingPartecipation();
        partecipation.setEvent(event);
        partecipation.setUser(user);

        if (request.getActivityIds() != null && !request.getActivityIds().isEmpty()) {
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
        }

        // Validate dayStart and dayEnd
        if (request.getDayStart() != null && request.getDayEnd() != null) {
            int totalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate()) + 1;
            if (request.getDayStart() < 1 || request.getDayStart() > totalDays ||
                request.getDayEnd() < 1 || request.getDayEnd() > totalDays ||
                request.getDayStart() > request.getDayEnd()) {
                throw new IllegalArgumentException("I giorni selezionati invalidi.");
            }
        }

        partecipation.setDayStart(request.getDayStart());
        partecipation.setDayEnd(request.getDayEnd());

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

        return partecipations.stream()
                .flatMap(p -> p.getChosenActivities().stream())
                .collect(java.util.stream.Collectors.groupingBy(
                        Activity::getId,
                        java.util.stream.Collectors.counting()
                ));
    }

    @Transactional
    public void deletePartecipation(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));

        TeamBuildingPartecipation partecipation = partecipationRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new IllegalArgumentException("Non hai partecipazioni registrate per questo evento."));

        partecipationRepository.delete(partecipation);
    }
}
