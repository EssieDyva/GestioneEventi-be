package com.gestioneEventi.controllers;

import com.gestioneEventi.dto.partecipation.CreatePartecipation;
import com.gestioneEventi.dto.partecipation.PartecipationDTO;
import com.gestioneEventi.dto.partecipation.UpdatePartecipation;
import com.gestioneEventi.models.Partecipation;
import com.gestioneEventi.models.User;
import com.gestioneEventi.services.PartecipationService;
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

import jakarta.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/api/partecipation")
@Tag(name = "Partecipazioni", description = "API per la gestione delle partecipazioni agli eventi")
@SecurityRequirement(name = "bearerAuth")
public class PartecipationController {

    @Autowired
    private PartecipationService partecipationService;

    @PostMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Crea nuove partecipazioni (EDITOR/ADMIN)",
        description = "Crea nuove partecipazioni per uno o più utenti ad un evento specifico. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partecipazioni create con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<PartecipationDTO>> createPartecipation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dati per la creazione delle partecipazioni",
                required = true,
                content = @Content(schema = @Schema(implementation = CreatePartecipation.class))
            )
            @RequestBody CreatePartecipation request) {
        List<Partecipation> created = partecipationService.createPartecipation(request);
        List<PartecipationDTO> partecipationDTOs = created.stream()
                .map(PartecipationDTO::new)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(partecipationDTOs);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Ottieni tutte le partecipazioni  (EDITOR/ADMIN)",
        description = "Recupera l'elenco completo di tutte le partecipazioni. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista partecipazioni recuperata con successo"),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<PartecipationDTO>> getAllPartecipations() {
        List<Partecipation> partecipations = partecipationService.getAllPartecipations();
        List<PartecipationDTO> partecipationDTOs = partecipations.stream()
                .map(PartecipationDTO::new)
                .toList();
        return ResponseEntity.ok(partecipationDTOs);
    }

    @GetMapping("/event/{eventId}")
    @Operation(
        summary = "Ottieni partecipazioni per evento",
        description = "Recupera tutte le partecipazioni relative ad un evento specifico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista partecipazioni recuperata con successo"),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<PartecipationDTO>> getPartecipationsByEvent(
            @Parameter(description = "ID dell'evento", required = true)
            @PathVariable @Positive Long eventId) {
        List<Partecipation> partecipations = partecipationService.getPartecipationsByEventId(eventId);
        List<PartecipationDTO> partecipationDTOs = partecipations.stream()
                .map(PartecipationDTO::new)
                .toList();
        return ResponseEntity.ok(partecipationDTOs);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Ottieni partecipazioni per utente",
        description = "Recupera tutte le partecipazioni di un utente specifico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista partecipazioni recuperata con successo"),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<PartecipationDTO>> getPartecipationsByUser(
            @Parameter(description = "ID dell'utente", required = true)
            @PathVariable @Positive Long userId) {
        List<Partecipation> partecipations = partecipationService.getPartecipationsByUserId(userId);
        List<PartecipationDTO> partecipationDTOs = partecipations.stream()
                .map(PartecipationDTO::new)
                .toList();
        return ResponseEntity.ok(partecipationDTOs);
    }

    @GetMapping("/me")
    @Operation(
        summary = "Ottieni le mie partecipazioni",
        description = "Recupera tutte le partecipazioni dell'utente corrente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista partecipazioni recuperata con successo"),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<PartecipationDTO>> getMyPartecipations(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        List<Partecipation> partecipations = partecipationService.getPartecipationsByUserId(user.getId());
        List<PartecipationDTO> partecipationDTOs = partecipations.stream()
                .map(PartecipationDTO::new)
                .toList();
        return ResponseEntity.ok(partecipationDTOs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR') or #user.id == @partecipationService.getOwnerId(#id)")
    @Operation(
        summary = "Aggiorna partecipazione",
        description = "Modifica lo stato di accettazione di una partecipazione esistente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partecipazione aggiornata con successo"),
        @ApiResponse(responseCode = "404", description = "Partecipazione non trovata", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dati non validi", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<PartecipationDTO> updatePartecipation(
            @Parameter(description = "ID della partecipazione da aggiornare", required = true)
            @PathVariable @Positive Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Nuovi dati della partecipazione",
                required = true,
                content = @Content(schema = @Schema(implementation = UpdatePartecipation.class))
            )
            @RequestBody UpdatePartecipation request,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Partecipation updated = partecipationService.updatePartecipation(request, id, user);
        return ResponseEntity.ok(new PartecipationDTO(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Elimina partecipazione (ADMIN)",
        description = "Rimuove una partecipazione dal sistema. Richiede ruolo ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Partecipazione eliminata con successo", content = @Content),
        @ApiResponse(responseCode = "404", description = "Partecipazione non trovata", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePartecipation(
            @Parameter(description = "ID della partecipazione da eliminare", required = true)
            @PathVariable @Positive Long id) {
        partecipationService.deletePartecipation(id);
    }
}
