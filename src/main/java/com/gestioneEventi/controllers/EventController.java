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

import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.User;
import com.gestioneEventi.services.EventService;

import jakarta.validation.constraints.Positive;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/user/me")
    public ResponseEntity<List<EventDTO>> getMyEvents(@AuthenticationPrincipal User user) {
        List<Event> events = eventService.getUserEvents(user.getId());
        List<EventDTO> eventDTOs = events.stream()
                .map(EventDTO::new)
                .toList();
        return ResponseEntity.ok(eventDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable @Positive Long id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(new EventDTO(event));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents()
                .stream()
                .map(EventDTO::new)
                .toList();
        return ResponseEntity.ok(events);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<EventDTO> createEvent(
            @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal User user) {
        Event created = eventService.createEvent(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new EventDTO(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable @Positive Long id,
            @RequestBody UpdateEventRequest request) {
        return eventService.updateEvent(id, request)
                .map(updated -> ResponseEntity.ok(new EventDTO(updated)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EDITOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable @Positive Long id) {
        eventService.deleteEvent(id);
    }
}