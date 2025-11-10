package com.gestioneEventi.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioneEventi.dto.ferie.CreateFerieRequest;
import com.gestioneEventi.dto.ferie.UpdateFerieRequest;
import com.gestioneEventi.exceptions.BusinessValidationException;
import com.gestioneEventi.exceptions.InsufficientPermissionException;
import com.gestioneEventi.exceptions.ResourceNotFoundException;
import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.EventType;
import com.gestioneEventi.models.Ferie;
import com.gestioneEventi.models.Status;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.EventRepository;
import com.gestioneEventi.repositories.FerieRepository;

@Service
public class FerieService {

    @Autowired
    private FerieRepository ferieRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public Ferie createFerie(CreateFerieRequest request, User user) {
        if (request.getEventId() == null) {
            throw new BusinessValidationException("Evento non specificato");
        }

        boolean canRequest = ferieRepository.canUserRequestFerieForEvent(
                request.getEventId(),
                user.getId());

        if (!canRequest) {
            throw new InsufficientPermissionException(
                    "Non puoi richiedere ferie per questo evento (non invitato o evento già iniziato)");
        }

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento", request.getEventId()));

        if (event.getEventType() != EventType.FERIE) {
            throw new BusinessValidationException("Le ferie possono essere richieste solo per eventi di tipo FERIE");
        }

        Ferie ferie = new Ferie();
        ferie.setTitle(request.getTitle());
        ferie.setStartDate(request.getStartDate());
        ferie.setEndDate(request.getEndDate());
        ferie.setEvent(event);
        ferie.setCreatedBy(user);
        ferie.setStatus(Status.APPROVED);

        validateFerieDates(ferie, event);

        return ferieRepository.save(ferie);
    }

    @Transactional(readOnly = true)
    public List<Ferie> getFerieByUserEmail(String email) {
        return ferieRepository.findByCreatedByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Ferie> getAllFerie() {
        return ferieRepository.findAll();
    }

    @Transactional
    public Ferie updateFerie(Long id, UpdateFerieRequest request) {
        Ferie ferie = ferieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ferie", id));

        Event event = eventRepository.findById(ferie.getEvent().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento associato", ferie.getEvent().getId()));

        ferie.setTitle(request.getTitle());
        ferie.setStartDate(request.getStartDate());
        ferie.setEndDate(request.getEndDate());

        validateFerieDates(ferie, event);

        return ferieRepository.save(ferie);
    }

    @Transactional
    public Ferie updateFerieStatus(Long id, Status status) {
        Ferie ferie = ferieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ferie", id));

        ferie.setStatus(status);
        return ferieRepository.save(ferie);
    }

    @Transactional
    public void deleteFerie(Long id) {
        if (!ferieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ferie", id);
        }
        ferieRepository.deleteById(id);
    }

    private void validateFerieDates(Ferie ferie, Event event) {
        LocalDate today = LocalDate.now();

        if (ferie.getStartDate() == null || ferie.getEndDate() == null) {
            throw new IllegalArgumentException("Date di inizio e fine sono obbligatorie");
        }
        if (ferie.getStartDate().isAfter(ferie.getEndDate())) {
            throw new IllegalArgumentException("La data di inizio deve essere precedente alla data di fine");
        }
        if (ferie.getStartDate().isBefore(today) || ferie.getEndDate().isBefore(today)) {
            throw new IllegalArgumentException("Le date non possono essere nel passato");
        }
        if (ferie.getStartDate().isBefore(event.getStartDate()) ||
                ferie.getEndDate().isAfter(event.getEndDate())) {
            throw new IllegalArgumentException("Le ferie devono rientrare nelle date dell'evento");
        }
    }
}