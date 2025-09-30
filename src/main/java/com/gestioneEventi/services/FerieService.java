package com.gestioneEventi.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.Ferie;
import com.gestioneEventi.repositories.EventRepository;
import com.gestioneEventi.repositories.FerieRepository;

@Service
public class FerieService {

    @Autowired
    private FerieRepository ferieRepository;

    @Autowired
    private EventRepository eventRepository;

    public void validateFerieDates(Ferie ferie, Event event) {
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
}
