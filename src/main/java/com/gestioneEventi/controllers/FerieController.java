package com.gestioneEventi.controllers;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Ferie", description = "API per la gestione delle richieste ferie")
@SecurityRequirement(name = "bearerAuth")
public class FerieController {

    @Autowired
    private FerieService ferieService;

    @PostMapping
    @Operation(
        summary = "Crea richiesta ferie",
        description = "Crea una nuova richiesta di ferie associata a un evento. L'utente deve essere invitato all'evento"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Richiesta ferie creata con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti o evento non accessibile", content = @Content),
        @ApiResponse(responseCode = "404", description = "Evento non trovato", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<FerieDTO> createFerie(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dati della richiesta ferie",
                required = true,
                content = @Content(schema = @Schema(implementation = CreateFerieRequest.class))
            )
            @Valid @RequestBody CreateFerieRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Ferie created = ferieService.createFerie(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new FerieDTO(created));
    }

    @GetMapping("/me")
    @Operation(
        summary = "Ottieni le mie richieste ferie",
        description = "Recupera tutte le richieste ferie dell'utente corrente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista ferie recuperata con successo"),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<FerieDTO>> getMyFerie(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        List<FerieDTO> ferie = ferieService.getFerieByUserEmail(user.getEmail())
                .stream()
                .map(FerieDTO::new)
                .toList();
        return ResponseEntity.ok(ferie);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Ottieni tutte le richieste ferie (EDITOR/ADMIN)",
        description = "Recupera l'elenco completo di tutte le richieste ferie. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista ferie recuperata con successo"),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<FerieDTO>> getAllFerie() {
        List<FerieDTO> ferie = ferieService.getAllFerie()
                .stream()
                .map(FerieDTO::new)
                .toList();
        return ResponseEntity.ok(ferie);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Aggiorna richiesta ferie",
        description = "Modifica i dettagli di una richiesta ferie esistente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Richiesta ferie aggiornata con successo"),
        @ApiResponse(responseCode = "404", description = "Richiesta ferie non trovata", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dati non validi", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<FerieDTO> updateFerie(
            @Parameter(description = "ID della richiesta ferie da aggiornare", required = true)
            @PathVariable @Positive Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Nuovi dati della richiesta ferie",
                required = true,
                content = @Content(schema = @Schema(implementation = UpdateFerieRequest.class))
            )
            @Valid @RequestBody UpdateFerieRequest request) {
        Ferie updated = ferieService.updateFerie(id, request);
        return ResponseEntity.ok(new FerieDTO(updated));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Aggiorna stato richiesta ferie (EDITOR/ADMIN)",
        description = "Approva o rifiuta una richiesta ferie. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stato aggiornato con successo"),
        @ApiResponse(responseCode = "404", description = "Richiesta ferie non trovata", content = @Content),
        @ApiResponse(responseCode = "400", description = "Stato non valido", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<FerieDTO> updateFerieStatus(
            @Parameter(description = "ID della richiesta ferie", required = true)
            @PathVariable @Positive Long id,
            @Parameter(description = "Nuovo stato (APPROVED o REJECTED)", required = true, schema = @Schema(implementation = Status.class))
            @RequestParam Status status) {
        Ferie updated = ferieService.updateFerieStatus(id, status);
        return ResponseEntity.ok(new FerieDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    @Operation(
        summary = "Elimina richiesta ferie (EDITOR/ADMIN)",
        description = "Rimuove una richiesta ferie dal sistema. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Richiesta ferie eliminata con successo", content = @Content),
        @ApiResponse(responseCode = "404", description = "Richiesta ferie non trovata", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFerie(
            @Parameter(description = "ID della richiesta ferie da eliminare", required = true)
            @PathVariable @Positive Long id) {
        ferieService.deleteFerie(id);
    }
}