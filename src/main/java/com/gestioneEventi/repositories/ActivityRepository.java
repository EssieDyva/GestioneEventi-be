package com.gestioneEventi.repositories;

import com.gestioneEventi.models.Activity;
import com.gestioneEventi.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByEvent(Event event);
}