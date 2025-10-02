package com.gestioneEventi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gestioneEventi.dto.ferie.CreateFerieRequest;
import com.gestioneEventi.dto.ferie.FerieDTO;
import com.gestioneEventi.dto.ferie.UpdateFerieRequest;
import com.gestioneEventi.models.Status;
import com.gestioneEventi.models.User;
import com.gestioneEventi.services.FerieService;

@RestController
@RequestMapping("/api/ferie")
public class FerieController {

    @Autowired
    private FerieService ferieService;

    @PostMapping
    public ResponseEntity<FerieDTO> createFerie(
            @RequestBody CreateFerieRequest request, 
            @AuthenticationPrincipal User user) {
        var created = ferieService.createFerie(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new FerieDTO(created));
    }

    @GetMapping("/user/me")
    public ResponseEntity<List<FerieDTO>> getMyFerie(Authentication authentication) {
        String email = authentication.getName();
        List<FerieDTO> ferie = ferieService.getFerieByUserEmail(email)
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
            @PathVariable Long id, 
            @RequestBody UpdateFerieRequest request) {
        return ferieService.updateFerie(id, request)
                .map(updated -> ResponseEntity.ok(new FerieDTO(updated)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<FerieDTO> updateFerieStatus(
            @PathVariable Long id, 
            @RequestParam Status status) {
        return ferieService.updateFerieStatus(id, status)
                .map(updated -> ResponseEntity.ok(new FerieDTO(updated)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<Void> deleteFerie(@PathVariable Long id) {
        if (ferieService.deleteFerie(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}