package com.gestioneEventi.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.Ferie;
import com.gestioneEventi.models.Status;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.EventRepository;
import com.gestioneEventi.repositories.FerieRepository;

@RestController
@RequestMapping("/api/ferie")
public class FerieController {

    @Autowired
    private FerieRepository ferieRepository;

    @Autowired
    private EventRepository eventRepository;

    @PostMapping
    public ResponseEntity<?> createFerie(@RequestBody Ferie ferie,
            @AuthenticationPrincipal User user) {
        // Recupera l'evento a cui l'utente vuole collegare la richiesta
        Event event = eventRepository.findById(ferie.getEvent().getId())
                .orElse(null);

        if (event == null) {
            return ResponseEntity.badRequest().body("Evento non trovato");
        }

        // Controlla se l'utente è in uno dei gruppi invitati e se l'evento è attivo
        boolean canRequest = event.getInvitedGroups().stream()
                .anyMatch(group -> group.getMembers().contains(user)) &&
                !LocalDate.now().isBefore(event.getStartDate()) &&
                !LocalDate.now().isAfter(event.getEndDate());

        if (!canRequest) {
            return ResponseEntity.status(403).body("Non puoi richiedere ferie per questo evento");
        }

        ferie.setCreatedBy(user); // associa l'utente
        ferie.setStatus(Status.APPROVED); // default
        return ResponseEntity.ok(ferieRepository.save(ferie));
    }

    @GetMapping("/user/me")
    public List<Ferie> getMyFerie(Authentication authentication) {
        String email = authentication.getName();
        return ferieRepository.findByCreatedByEmail(email);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public List<Ferie> getAllFerie() {
        return ferieRepository.findAll();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ferie> updateFerie(@PathVariable Long id, @RequestBody Ferie ferieDetails) {
        return ferieRepository.findById(id).map(ferie -> {
            ferie.setTitle(ferieDetails.getTitle());
            ferie.setStartDate(ferieDetails.getStartDate());
            ferie.setEndDate(ferieDetails.getEndDate());
            return ResponseEntity.ok(ferieRepository.save(ferie));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<?> updateFerieStatus(@PathVariable Long id, @RequestParam Status status) {
        return ferieRepository.findById(id).map(ferie -> {
            ferie.setStatus(status);
            ferieRepository.save(ferie);
            return ResponseEntity.ok(ferie);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (!ferieRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ferieRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}