package com.gestioneEventi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gestioneEventi.dto.ferie.CreateFerieRequest;
import com.gestioneEventi.dto.ferie.FerieDTO;
import com.gestioneEventi.dto.ferie.UpdateFerieRequest;
import com.gestioneEventi.models.Ferie;
import com.gestioneEventi.models.Status;
import com.gestioneEventi.models.User;
import com.gestioneEventi.services.FerieService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/ferie")
public class FerieController {

    @Autowired
    private FerieService ferieService;

    @PostMapping
    public ResponseEntity<FerieDTO> createFerie(
            @Valid @RequestBody CreateFerieRequest request,
            @AuthenticationPrincipal User user) {
        Ferie created = ferieService.createFerie(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new FerieDTO(created));
    }

    @GetMapping("/user/me")
    public ResponseEntity<List<FerieDTO>> getMyFerie(@AuthenticationPrincipal User user) {
        List<FerieDTO> ferie = ferieService.getFerieByUserEmail(user.getEmail())
                .stream()
                .map(FerieDTO::new)
                .toList();
        return ResponseEntity.ok(ferie);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<FerieDTO>> getAllFerie() {
        List<FerieDTO> ferie = ferieService.getAllFerie()
                .stream()
                .map(FerieDTO::new)
                .toList();
        return ResponseEntity.ok(ferie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FerieDTO> updateFerie(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateFerieRequest request) {
        Ferie updated = ferieService.updateFerie(id, request);
        return ResponseEntity.ok(new FerieDTO(updated));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<FerieDTO> updateFerieStatus(
            @PathVariable @Positive Long id,
            @RequestParam Status status) {
        Ferie updated = ferieService.updateFerieStatus(id, status);
        return ResponseEntity.ok(new FerieDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFerie(@PathVariable @Positive Long id) {
        ferieService.deleteFerie(id);
    }
}