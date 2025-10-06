package com.gestioneEventi.repositories;

import com.gestioneEventi.models.Event;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
                SELECT DISTINCT e
                FROM Event e
                JOIN e.invitedGroups g
                JOIN g.members u
                WHERE u.id = :id
            """)
    List<Event> findAllByUserId(@Param("id") Long id);
}