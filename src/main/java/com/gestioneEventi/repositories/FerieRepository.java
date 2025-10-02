package com.gestioneEventi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestioneEventi.models.Ferie;

import java.util.List;

@Repository
public interface FerieRepository extends JpaRepository<Ferie, Long> {
    List<Ferie> findByCreatedByEmail(String email);

    @Query("""
            SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
            FROM Event e
            JOIN e.invitedGroups g
            JOIN g.members u
            WHERE e.id = :eventId
            AND u.id = :userId
            AND e.startDate > CURRENT_DATE
            """)
    boolean canUserRequestFerieForEvent(@Param("eventId") Long eventId, @Param("userId") Long userId);
}