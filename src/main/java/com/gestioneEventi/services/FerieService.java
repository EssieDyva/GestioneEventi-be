package com.gestioneEventi.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioneEventi.dto.ferie.CreateFerieRequest;
import com.gestioneEventi.dto.ferie.UpdateFerieRequest;
import com.gestioneEventi.exceptions.InsufficientPermissionException;
import com.gestioneEventi.exceptions.ResourceNotFoundException;
import com.gestioneEventi.models.Event;
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
            throw new IllegalArgumentException("Evento non specificato");
        }

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Evento", request.getEventId()));

        validateUserCanRequestFerie(user, event);

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
    public Optional<Ferie> updateFerie(Long id, UpdateFerieRequest request) {
        return ferieRepository.findById(id).map(ferie -> {
            Event event = eventRepository.findById(ferie.getEvent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Evento associato non trovato"));

            ferie.setTitle(request.getTitle());
            ferie.setStartDate(request.getStartDate());
            ferie.setEndDate(request.getEndDate());

            validateFerieDates(ferie, event);

            return ferieRepository.save(ferie);
        });
    }

    @Transactional
    public Optional<Ferie> updateFerieStatus(Long id, Status status) {
        return ferieRepository.findById(id).map(ferie -> {
            ferie.setStatus(status);
            return ferieRepository.save(ferie);
        });
    }

    @Transactional
    public boolean deleteFerie(Long id) {
        if (ferieRepository.existsById(id)) {
            ferieRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validateUserCanRequestFerie(User user, Event event) {
        boolean isInvited = ferieRepository.isUserInvitedToEvent(event.getId(), user.getId());

        if (!isInvited) {
            throw new InsufficientPermissionException("Non sei invitato a questo evento");
        }

        if (!LocalDate.now().isBefore(event.getStartDate())) {
            throw new IllegalArgumentException("L'evento è già iniziato, non puoi più richiedere ferie");
        }
    }

    private void validateFerieDates(Ferie ferie, Event event) {
        LocalDate today = LocalDate.now();

        if (ferie.getStartDate() == null || ferie.getEndDate() == null) {
            throw new IllegalArgumentException("Date di inizio e fine sono obbligatorie");
        }
        if (!ferie.getStartDate().isBefore(ferie.getEndDate())) {
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