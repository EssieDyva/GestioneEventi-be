package com.gestioneEventi.controllers;

import com.gestioneEventi.dto.activity.ActivityDTO;
import com.gestioneEventi.dto.activity.CreateActivityRequest;
import com.gestioneEventi.models.Activity;
import com.gestioneEventi.models.User;
import com.gestioneEventi.services.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events/{eventId}/activities")
@Tag(name = "Activity", description = "Gestione delle attività per eventi TEAM_BUILDING")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @Operation(summary = "Aggiunge una nuova attività ad un evento TEAM_BUILDING")
    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(
            @PathVariable Long eventId,
            @RequestBody CreateActivityRequest request,
            @AuthenticationPrincipal User currentUser) {

        Activity created = activityService.createActivity(eventId, request, currentUser);
        return ResponseEntity.ok(new ActivityDTO(created));
    }

    @Operation(summary = "Elenca tutte le attività associate ad un evento TEAM_BUILDING")
    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getActivities(@PathVariable Long eventId) {
        List<Activity> activities = activityService.getActivitiesByEvent(eventId);
        List<ActivityDTO> activityDTOs = activities.stream()
                .map(ActivityDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(activityDTOs);
    }

    @Operation(summary = "Modifica un'attività (solo il creatore può farlo)")
    @PutMapping("/{activityId}")
    public ResponseEntity<ActivityDTO> updateActivity(
            @PathVariable Long eventId,
            @PathVariable Long activityId,
            @RequestBody CreateActivityRequest request,
            @AuthenticationPrincipal User user) {

        Activity updated = activityService.updateActivity(activityId, request, user);
        return ResponseEntity.ok(new ActivityDTO(updated));
    }

    @Operation(summary = "Elimina un'attività (solo il creatore può farlo)")
    @DeleteMapping("/{activityId}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteActivity(
            @PathVariable Long eventId,
            @PathVariable Long activityId,
            @AuthenticationPrincipal User currentUser) {

        activityService.deleteActivity(activityId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
