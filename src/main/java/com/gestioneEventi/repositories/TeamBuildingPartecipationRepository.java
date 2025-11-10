package com.gestioneEventi.repositories;

import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.TeamBuildingPartecipation;
import com.gestioneEventi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamBuildingPartecipationRepository extends JpaRepository<TeamBuildingPartecipation, Long> {

    List<TeamBuildingPartecipation> findByEvent(Event event);

    List<TeamBuildingPartecipation> findByUserAndEvent(User user, Event event);

    void deleteAllByEvent(Event event);
}