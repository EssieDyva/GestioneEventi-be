package com.gestioneEventi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestioneEventi.models.Ferie;
import com.gestioneEventi.repositories.FerieRepository;

@RestController
@RequestMapping("/api/ferie")
public class FerieController {

    @Autowired
    private FerieRepository ferieRepository;
    
    @PostMapping
    public Ferie createFerie(@RequestBody Ferie ferie) {
        return ferieRepository.save(ferie);
    }

    @GetMapping("/user/{id}")
    public List<Ferie> getAllFerieByUser(@PathVariable Long id) {
        return ferieRepository.findByCreatedById(id);
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