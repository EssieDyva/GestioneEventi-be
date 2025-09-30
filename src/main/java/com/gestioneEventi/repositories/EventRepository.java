package com.gestioneEventi.repositories;

import com.gestioneEventi.models.Event;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @EntityGraph(attributePaths = { "invitedGroups", "invitedGroups.members" })
    Optional<Event> findById(Long id);
}