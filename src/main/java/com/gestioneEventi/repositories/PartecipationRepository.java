package com.gestioneEventi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestioneEventi.models.Partecipation;

public interface PartecipationRepository extends JpaRepository<Partecipation, Long> {
    
}