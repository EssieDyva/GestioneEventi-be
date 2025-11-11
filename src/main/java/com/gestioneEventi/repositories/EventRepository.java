package com.gestioneEventi.repositories;

import com.gestioneEventi.models.Event;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Event entity operations.
 * Provides standard CRUD operations and custom queries for event management.
 * Uses EntityGraph to optimize loading of related entities.
 *
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Finds all events with eager loading of invited users, creator, and creator's role.
     * Uses EntityGraph to optimize database queries and prevent N+1 problems.
     *
     * @return List of all events with related entities loaded
     */
    @EntityGraph(attributePaths = { "invitedUsers", "createdBy", "createdBy.role" })
    @Override
    List<Event> findAll();

    /**
     * Finds an event by ID with eager loading of invited users, creator, and creator's role.
     * Uses EntityGraph to optimize database queries.
     *
     * @param id The ID of the event to find
     * @return Optional containing the event if found, with related entities loaded
     */
    @EntityGraph(attributePaths = { "invitedUsers", "createdBy", "createdBy.role" })
    @Override
    Optional<Event> findById(Long id);

    /**
     * Finds all events where a specific user is invited.
     * Uses a custom JPQL query to find events by invited user ID.
     *
     * @param id The ID of the user to find events for
     * @return List of events where the specified user is invited
     */
    @Query("""
                SELECT DISTINCT e
                FROM Event e
                JOIN e.invitedUsers iu
                WHERE iu.id = :id
            """)
    List<Event> findAllByUserId(@Param("id") Long id);
}
