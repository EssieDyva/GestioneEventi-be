package com.gestioneEventi.controllers;

import com.gestioneEventi.dto.event.CreateEventRequest;
import com.gestioneEventi.dto.event.EventDTO;
import com.gestioneEventi.dto.event.UpdateEventRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.gestioneEventi.models.User;
import com.gestioneEventi.services.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        return ResponseEntity.ok(
                eventService.getAllEvents()
                        .stream()
                        .map(EventDTO::new)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(event -> ResponseEntity.ok(new EventDTO(event)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<EventDTO> createEvent(
            @RequestBody CreateEventRequest request, 
            @AuthenticationPrincipal User user) {
        var created = eventService.createEvent(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EventDTO(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id, 
            @RequestBody UpdateEventRequest request) {
        return eventService.updateEvent(id, request)
                .map(updated -> ResponseEntity.ok(new EventDTO(updated)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (eventService.deleteEvent(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}