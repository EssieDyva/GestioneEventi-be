package com.gestioneEventi.controllers;

import com.gestioneEventi.dto.teambuilding.CreateTeamBuildingPartecipationRequest;
import com.gestioneEventi.dto.teambuilding.TeamBuildingPartecipationDTO;
import com.gestioneEventi.models.TeamBuildingPartecipation;
import com.gestioneEventi.models.User;
import com.gestioneEventi.services.TeamBuildingPartecipationService;
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
@RequestMapping("/api/events/{eventId}/partecipations")
@Tag(name = "Team Building Partecipations", description = "Gestione delle partecipazioni agli eventi di tipo TEAM_BUILDING")
public class TeamBuildingPartecipationController {

    @Autowired
    private TeamBuildingPartecipationService partecipationService;

    @Operation(summary = "Crea una partecipazione a un evento TEAM_BUILDING")
    @PostMapping
    public ResponseEntity<TeamBuildingPartecipationDTO> createPartecipation(
            @PathVariable Long eventId,
            @RequestBody CreateTeamBuildingPartecipationRequest request,
            @AuthenticationPrincipal User currentUser) {

        TeamBuildingPartecipation partecipation = partecipationService.createPartecipation(eventId, request, currentUser);
        return ResponseEntity.ok(new TeamBuildingPartecipationDTO(partecipation));
    }

    @Operation(summary = "Elenca tutte le partecipazioni di un evento TEAM_BUILDING")
    @GetMapping
    public ResponseEntity<List<TeamBuildingPartecipationDTO>> getPartecipations(@PathVariable Long eventId) {
        List<TeamBuildingPartecipation> partecipations = partecipationService.getPartecipationsForEvent(eventId);
        List<TeamBuildingPartecipationDTO> partecipationDTOs = partecipations.stream()
                .map(TeamBuildingPartecipationDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(partecipationDTOs);
    }

    @Operation(summary = "Ottieni la popolarità delle attività per un evento TEAM_BUILDING (EDITOR/ADMIN)")
    @GetMapping("/popularity")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<java.util.Map<Long, Long>> getActivityPopularity(@PathVariable Long eventId) {
        java.util.Map<Long, Long> popularity = partecipationService.getActivityPopularity(eventId);
        return ResponseEntity.ok(popularity);
    }

    @Operation(summary = "Annulla la partecipazione di un utente al TEAM_BUILDING")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyPartecipation(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User currentUser) {

        partecipationService.deletePartecipation(eventId, currentUser);
        return ResponseEntity.noContent().build();
    }
}