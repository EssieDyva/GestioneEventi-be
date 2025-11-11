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

/**
 * Service class for managing activity-related operations.
 * Handles CRUD operations for activities within team building events.
 * Ensures activities can only be created for team building event types.
 *
 */
@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private EventRepository eventRepository;

    /**
     * Creates a new activity for a team building event.
     * Validates that the event is of type TEAM_BUILDING before creating the activity.
     * Sets isCustom to true if the creator is a USER, false otherwise.
     *
     * @param eventId The ID of the event to add the activity to
     * @param request The activity creation request containing name
     * @param creator The user creating the activity
     * @return The created and saved activity
     * @throws ResourceNotFoundException if the event is not found
     * @throws IllegalArgumentException if the event is not of type TEAM_BUILDING
     */
    @Transactional
    public Activity createActivity(Long eventId, CreateActivityRequest request, User creator) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));

        if (event.getEventType() != EventType.TEAM_BUILDING) {
            throw new IllegalArgumentException("Le attività sono consentite solo per eventi di tipo TEAM_BUILDING.");
        }

        Activity activity = new Activity();
        activity.setName(request.getName());
        activity.setCreatedBy(creator);
        activity.setEvent(event);
        activity.setCustom(creator.getRole() == Role.USER);

        return activityRepository.save(activity);
    }

    /**
     * Retrieves all activities associated with a specific event.
     *
     * @param eventId The ID of the event to get activities for
     * @return List of activities for the specified event
     * @throws ResourceNotFoundException if the event is not found
     */
    @Transactional(readOnly = true)
    public List<Activity> getActivitiesByEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento", eventId));

        return activityRepository.findByEvent(event);
    }

    /**
     * Updates an existing activity.
     * Only the creator of the activity can modify it.
     *
     * @param activityId The ID of the activity to update
     * @param request The update request containing new name
     * @param currentUser The user attempting to update the activity
     * @return The updated activity
     * @throws ResourceNotFoundException if the activity is not found
     * @throws IllegalArgumentException if the current user is not the creator
     */
    @Transactional
    public Activity updateActivity(Long activityId, CreateActivityRequest request, User currentUser) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Attività", activityId));

        if (!activity.getCreatedBy().equals(currentUser)) {
            throw new IllegalArgumentException("Non puoi modificare attività create da altri utenti.");
        }

        activity.setName(request.getName());
        return activityRepository.save(activity);
    }

    /**
     * Deletes an activity.
     * Only the creator of the activity can delete it.
     *
     * @param activityId The ID of the activity to delete
     * @param currentUser The user attempting to delete the activity
     * @throws ResourceNotFoundException if the activity is not found
     * @throws IllegalArgumentException if the current user is not the creator
     */
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
