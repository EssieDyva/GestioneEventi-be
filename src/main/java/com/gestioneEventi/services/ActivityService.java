package com.gestioneEventi.services;

import com.gestioneEventi.dto.activity.CreateActivityRequest;
import com.gestioneEventi.exceptions.ResourceNotFoundException;
import com.gestioneEventi.models.*;
import com.gestioneEventi.repositories.ActivityRepository;
import com.gestioneEventi.repositories.EventRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public Activity createActivity(Long eventId, CreateActivityRequest request, User creator) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));

        if (event.getEventType() != EventType.TEAM_BUILDING) {
            throw new IllegalArgumentException("Le attività sono consentite solo per eventi di tipo TEAM_BUILDING.");
        }

        Activity activity = new Activity();
        activity.setName(request.getName());
        activity.setDescription(request.getDescription());
        activity.setCreatedBy(creator);
        activity.setEvent(event);
        activity.setCustom(false);

        return activityRepository.save(activity);
    }

    @Transactional(readOnly = true)
    public List<Activity> getActivitiesByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));

        return activityRepository.findByEvent(event);
    }

    @Transactional
    public void deleteActivity(Long activityId, User currentUser) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Attività", activityId));

        if (!activity.getCreatedBy().equals(currentUser)) {
            throw new IllegalArgumentException("Non puoi eliminare attività create da altri utenti.");
        }

        activityRepository.delete(activity);
    }
}