package com.gestioneEventi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestioneEventi.models.Partecipation;

public interface PartecipationRepository extends JpaRepository<Partecipation, Long> {

    List<Partecipation> findByEventIdAndUserIdIn(Long eventId, List<Long> userIds);    
}