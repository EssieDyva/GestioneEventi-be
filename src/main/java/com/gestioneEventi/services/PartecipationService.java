package com.gestioneEventi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.gestioneEventi.dto.partecipation.CreatePartecipation;
import com.gestioneEventi.exceptions.BusinessValidationException;
import com.gestioneEventi.exceptions.ResourceNotFoundException;
import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.Partecipation;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.EventRepository;
import com.gestioneEventi.repositories.PartecipationRepository;

public class PartecipationService {

    @Autowired
    private PartecipationRepository partecipationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public Partecipation createPartecipation(CreatePartecipation partecipation, User user) {
        if (partecipation.getEventId() == null)
            throw new BusinessValidationException("Evento non specificato");

        Event event = eventRepository.findById(partecipation.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento", partecipation.getEventId()));

        Partecipation userPartecipation = new Partecipation();
        userPartecipation.setIsEventAccepted(null);
        userPartecipation.setEvent(event);
        userPartecipation.setUser(user);

        return partecipationRepository.save(userPartecipation);
    }
}
