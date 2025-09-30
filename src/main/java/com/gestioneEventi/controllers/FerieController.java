package com.gestioneEventi.controllers;

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

import com.gestioneEventi.models.Ferie;
import com.gestioneEventi.models.Status;
import com.gestioneEventi.models.User;
import com.gestioneEventi.services.FerieService;

@RestController
@RequestMapping("/api/ferie")
public class FerieController {

    @Autowired
    private FerieService ferieService;

    @PostMapping
    public ResponseEntity<?> createFerie(@RequestBody Ferie ferie, @AuthenticationPrincipal User user) {
        try {
            Ferie created = ferieService.createFerie(ferie, user);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/user/me")
    public ResponseEntity<List<Ferie>> getMyFerie(Authentication authentication) {
        String email = authentication.getName();
        List<Ferie> ferie = ferieService.getFerieByUserEmail(email);
        return ResponseEntity.ok(ferie);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<Ferie>> getAllFerie() {
        List<Ferie> ferie = ferieService.getAllFerie();
        return ResponseEntity.ok(ferie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFerie(@PathVariable Long id, @RequestBody Ferie ferieDetails) {
        try {
            Ferie updated = ferieService.updateFerie(id, ferieDetails);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<?> updateFerieStatus(@PathVariable Long id, @RequestParam Status status) {
        try {
            Ferie updated = ferieService.updateFerieStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<?> deleteFerie(@PathVariable Long id) {
        try {
            ferieService.deleteFerie(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}