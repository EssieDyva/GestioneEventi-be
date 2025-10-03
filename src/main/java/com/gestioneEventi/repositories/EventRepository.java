package com.gestioneEventi.repositories;

import com.gestioneEventi.models.Event;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(attributePaths = { "invitedGroups", "invitedGroups.members", "createdBy", "createdBy.role" })
    @Override
    List<Event> findAll();

    @EntityGraph(attributePaths = { "invitedGroups", "invitedGroups.members", "createdBy", "createdBy.role" })
    @Override
    Optional<Event> findById(Long id);

    @Query("""
            SELECT e FROM event e
            INNER JOIN event_groups eg ON e.id = eg.event_id
            INNER JOIN users u ON eg.group_id = u.group_id
            WHERE u.id = :id
            """)
    List<Event> findAllByUserId(Long id);
}