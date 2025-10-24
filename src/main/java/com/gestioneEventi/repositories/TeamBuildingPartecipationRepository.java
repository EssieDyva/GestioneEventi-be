package com.gestioneEventi.repositories;

import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.TeamBuildingPartecipation;
import com.gestioneEventi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamBuildingPartecipationRepository extends JpaRepository<TeamBuildingPartecipation, Long> {

    List<TeamBuildingPartecipation> findByEvent(Event event);

    Optional<TeamBuildingPartecipation> findByUserAndEvent(User user, Event event);

    void deleteAllByEvent(Event event);
}