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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestioneEventi.dto.userGroup.CreateGroupRequest;
import com.gestioneEventi.dto.userGroup.UpdateGroupRequest;
import com.gestioneEventi.dto.userGroup.UserGroupDTO;
import com.gestioneEventi.models.User;
import com.gestioneEventi.models.UserGroup;
import com.gestioneEventi.services.UserGroupService;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Gruppi", description = "API per la gestione dei gruppi utenti")
@SecurityRequirement(name = "bearerAuth")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    @GetMapping("/me")
    @Operation(
        summary = "Ottieni i miei gruppi",
        description = "Recupera tutti i gruppi di cui l'utente corrente è membro"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista gruppi recuperata con successo"),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<UserGroupDTO>> getMyGroups(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        List<UserGroupDTO> groups = userGroupService.getMyGroups(user.getId())
                .stream()
                .map(UserGroupDTO::new)
                .toList();
        return ResponseEntity.ok(groups);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Ottieni tutti i gruppi (EDITOR/ADMIN)",
        description = "Recupera l'elenco completo di tutti i gruppi. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista gruppi recuperata con successo"),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<UserGroupDTO>> getAllGroups() {
        List<UserGroup> groups = userGroupService.getAllGroups();
        List<UserGroupDTO> dtoList = groups.stream()
                .map(UserGroupDTO::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Ottieni gruppo per ID (EDITOR/ADMIN)",
        description = "Recupera i dettagli di un gruppo specifico. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gruppo trovato"),
        @ApiResponse(responseCode = "404", description = "Gruppo non trovato", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<?> getGroup(
            @Parameter(description = "ID del gruppo", required = true)
            @PathVariable Long id) {
        try {
            UserGroup group = userGroupService.getGroupById(id);
            return ResponseEntity.ok(group);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Crea un nuovo gruppo (EDITOR/ADMIN)",
        description = "Crea un nuovo gruppo con i membri specificati. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gruppo creato con successo"),
        @ApiResponse(responseCode = "400", description = "Dati non validi o utenti non trovati", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<?> createGroup(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Dati del nuovo gruppo",
                required = true,
                content = @Content(schema = @Schema(implementation = CreateGroupRequest.class))
            )
            @RequestBody CreateGroupRequest request) {
        try {
            UserGroup group = userGroupService.createGroup(
                    request.getGroupName(),
                    request.getMemberEmails());
            return ResponseEntity.ok(new UserGroupDTO(group));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Aggiorna un gruppo (EDITOR/ADMIN)",
        description = "Modifica nome e membri di un gruppo esistente. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Gruppo aggiornato con successo"),
        @ApiResponse(responseCode = "404", description = "Gruppo non trovato", content = @Content),
        @ApiResponse(responseCode = "400", description = "Dati non validi", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<UserGroupDTO> updateGroup(
            @Parameter(description = "ID del gruppo da aggiornare", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Nuovi dati del gruppo",
                required = true,
                content = @Content(schema = @Schema(implementation = UpdateGroupRequest.class))
            )
            @RequestBody UpdateGroupRequest request) {
        return userGroupService.updateGroup(id, request)
                .map(updated -> ResponseEntity.ok(new UserGroupDTO(updated)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Elimina un gruppo (EDITOR/ADMIN)",
        description = "Rimuove un gruppo dal sistema. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Gruppo eliminato con successo", content = @Content),
        @ApiResponse(responseCode = "404", description = "Gruppo non trovato", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<?> deleteGroup(
            @Parameter(description = "ID del gruppo da eliminare", required = true)
            @PathVariable Long id) {
        try {
            userGroupService.deleteGroup(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}