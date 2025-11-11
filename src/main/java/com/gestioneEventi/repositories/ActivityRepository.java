package com.gestioneEventi.repositories;

import com.gestioneEventi.models.Activity;
import com.gestioneEventi.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for Activity entity operations.
 * Provides standard CRUD operations and custom queries for activity management.
 *
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    /**
     * Finds all activities associated with a specific event.
     *
     * @param event The event to find activities for
     * @return List of activities for the specified event
     */
    List<Activity> findByEvent(Event event);
}
