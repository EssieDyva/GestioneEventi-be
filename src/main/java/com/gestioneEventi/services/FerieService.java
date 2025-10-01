package com.gestioneEventi.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Ferie createFerie(Ferie ferie, User user) {
        if (ferie.getEvent() == null || ferie.getEvent().getId() == null) {
            throw new IllegalArgumentException("Evento non specificato");
        }

        Event event = eventRepository.findById(ferie.getEvent().getId())
                .orElseThrow(() -> new IllegalArgumentException("Evento non trovato"));

        // Verifica che l'utente possa richiedere ferie per questo evento
        validateUserCanRequestFerie(user, event);

        // Valida le date
        validateFerieDates(ferie, event);

        ferie.setCreatedBy(user);
        ferie.setStatus(Status.APPROVED); // Approvazione automatica

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
    public Ferie updateFerie(Long id, Ferie ferieDetails) {
        Ferie ferie = ferieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ferie non trovate"));

        Event event = eventRepository.findById(ferie.getEvent().getId())
                .orElseThrow(() -> new IllegalArgumentException("Evento associato non trovato"));

        validateFerieDates(ferieDetails, event);

        ferie.setTitle(ferieDetails.getTitle());
        ferie.setStartDate(ferieDetails.getStartDate());
        ferie.setEndDate(ferieDetails.getEndDate());

        return ferieRepository.save(ferie);
    }

    @Transactional
    public Ferie updateFerieStatus(Long id, Status status) {
        Ferie ferie = ferieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ferie non trovate"));

        ferie.setStatus(status);
        return ferieRepository.save(ferie);
    }

    @Transactional
    public void deleteFerie(Long id) {
        if (!ferieRepository.existsById(id)) {
            throw new IllegalArgumentException("Ferie non trovate");
        }
        ferieRepository.deleteById(id);
    }

    private void validateUserCanRequestFerie(User user, Event event) {
        // OTTIMIZZATO: Query singola invece di caricare gruppi e membri
        boolean isInvited = ferieRepository.isUserInvitedToEvent(event.getId(), user.getId());

        if (!isInvited) {
            throw new IllegalArgumentException("Non sei invitato a questo evento");
        }

        // Verifica che l'evento non sia già iniziato
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