package com.gestioneEventi.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioneEventi.dto.partecipation.CreatePartecipation;
import com.gestioneEventi.dto.partecipation.UpdatePartecipation;
import com.gestioneEventi.exceptions.BusinessValidationException;
import com.gestioneEventi.exceptions.ResourceNotFoundException;
import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.Partecipation;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.EventRepository;
import com.gestioneEventi.repositories.PartecipationRepository;
import com.gestioneEventi.repositories.UserRepository;

@Service
public class PartecipationService {

    @Autowired
    private PartecipationRepository partecipationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<Partecipation> createPartecipation(CreatePartecipation request) {
        if (request.getEventId() == null)
            throw new BusinessValidationException("Evento non specificato");

        List<User> foundUsers = userRepository.findAllById(request.getUserIds());
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento", request.getEventId()));

        List<Partecipation> partecipations = foundUsers.stream()
                .map(user -> {
                    Partecipation p = new Partecipation();
                    p.setIsEventAccepted(null);
                    p.setEvent(event);
                    p.setUser(user);
                    return p;
                })
                .toList();

        return partecipationRepository.saveAll(partecipations);
    }

    @Transactional
    public List<Partecipation> createPartecipation(Long eventId, List<Long> userIds) {
        CreatePartecipation request = new CreatePartecipation();
        request.setEventId(eventId);
        request.setUserIds(userIds);
        return createPartecipation(request);
    }

    @Transactional
    public Partecipation updatePartecipation(UpdatePartecipation request, Long id) {
        Partecipation partecipation = partecipationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partecipazione", id));

        partecipation.setIsEventAccepted(request.getIsEventAccepted());

        return partecipationRepository.save(partecipation);
    }
}