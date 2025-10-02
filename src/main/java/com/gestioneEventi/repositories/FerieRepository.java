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

    /**
     * Verifica se un utente può richiedere ferie per un evento
     * in una singola query invece di caricare tutti i gruppi e membri
     */
    @Query("""
            SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
            FROM Event e
            JOIN e.invitedGroups g
            JOIN g.members u
            WHERE e.id = :eventId
            AND u.id = :userId
            """)
    boolean isUserInvitedToEvent(@Param("eventId") Long eventId, @Param("userId") Long userId);
}