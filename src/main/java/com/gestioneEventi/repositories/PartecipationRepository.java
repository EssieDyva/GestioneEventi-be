package com.gestioneEventi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.Partecipation;

public interface PartecipationRepository extends JpaRepository<Partecipation, Long> {

    List<Partecipation> findByEventIdAndUserIdIn(Long eventId, List<Long> userIds);

    List<Partecipation> findByEventId(Long eventId);

    List<Partecipation> findByUserId(Long userId);

    void deleteAllByEvent(Event event);
}
