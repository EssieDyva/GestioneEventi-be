package com.gestioneEventi.controllers;

import com.gestioneEventi.dto.event.CreateEventRequest;
import com.gestioneEventi.dto.event.EventDTO;
import com.gestioneEventi.dto.event.UpdateEventRequest;
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

import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.User;
import com.gestioneEventi.services.EventService;

import jakarta.validation.constraints.Positive;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@Tag(name = "Eventi", description = "API per la gestione degli eventi")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/me")
    @Operation(
        summary = "Ottieni i miei eventi",
        description = "Recupera tutti gli eventi a cui l'utente corrente è invitato"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista eventi recuperata con successo"),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<EventDTO>> getMyEvents(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        List<Event> events = eventService.getUserEvents(user.getId());
        List<EventDTO> eventDTOs = events.stream()
                .map(EventDTO::new)
                .toList();
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Ottieni evento per ID",
        description = "Recupera i dettagli di un evento specifico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento trovato"),
        @ApiResponse(responseCode = "404", description = "Evento non trovato", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<EventDTO> getEvent(
            @Parameter(description = "ID dell'evento", required = true)
            @PathVariable @Positive Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(new EventDTO(event));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Ottieni tutti gli eventi (EDITOR/ADMIN)",
        description = "Recupera l'elenco completo di tutti gli eventi. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista eventi recuperata con successo"),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents()
                .stream()
                .map(EventDTO::new)
                .toList();
        return ResponseEntity.ok(events);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Crea un nuovo evento (EDITOR/ADMIN)",
        description = "Crea un nuovo evento con i dettagli forniti. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Evento creato con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<EventDTO> createEvent(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dati del nuovo evento",
                required = true,
                content = @Content(schema = @Schema(implementation = CreateEventRequest.class))
            )
            @RequestBody CreateEventRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        Event created = eventService.createEvent(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EventDTO(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Aggiorna un evento (EDITOR/ADMIN)",
        description = "Modifica i dettagli di un evento esistente. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento aggiornato con successo"),
        @ApiResponse(responseCode = "404", description = "Evento non trovato", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dati non validi", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<EventDTO> updateEvent(
            @Parameter(description = "ID dell'evento da aggiornare", required = true)
            @PathVariable @Positive Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Nuovi dati dell'evento",
                required = true,
                content = @Content(schema = @Schema(implementation = UpdateEventRequest.class))
            )
            @RequestBody UpdateEventRequest request) {
        return eventService.updateEvent(id, request)
                .map(updated -> ResponseEntity.ok(new EventDTO(updated)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    @Operation(
        summary = "Elimina un evento (EDITOR/ADMIN)",
        description = "Rimuove un evento dal sistema. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Evento eliminato con successo", content = @Content),
        @ApiResponse(responseCode = "404", description = "Evento non trovato", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(
            @Parameter(description = "ID dell'evento da eliminare", required = true)
            @PathVariable @Positive Long id) {
        eventService.deleteEvent(id);
    }
}