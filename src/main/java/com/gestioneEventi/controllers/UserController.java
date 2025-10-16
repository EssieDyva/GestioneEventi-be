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
import org.springframework.web.bind.annotation.*;

import com.gestioneEventi.dto.UpdateUserRoleRequest;
import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.models.User;
import com.gestioneEventi.services.UserService;

@RestController
@RequestMapping("/api/user")
@Tag(name = "Utenti", description = "API per la gestione degli utenti")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    @Operation(
        summary = "Ottieni tutti gli utenti (EDITOR/ADMIN)",
        description = "Recupera l'elenco completo di tutti gli utenti con i loro gruppi. Richiede ruolo EDITOR o ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista utenti recuperata con successo"),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> usersDTO = userService.getAllUsersWithGroups()
                .stream()
                .map(UserDTO::new)
                .toList();
        return ResponseEntity.ok(usersDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(
        summary = "Aggiorna ruolo utente (ADMIN)",
        description = "Modifica il ruolo di un utente (USER, EDITOR, ADMIN). Richiede ruolo ADMIN"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ruolo utente aggiornato con successo"),
        @ApiResponse(responseCode = "404", description = "Utente non trovato", content = @Content),
        @ApiResponse(responseCode = "400", description = "Ruolo non valido", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permessi insufficienti", content = @Content),
        @ApiResponse(responseCode = "401", description = "Non autenticato", content = @Content)
    })
    public ResponseEntity<UserDTO> updateUserRole(
            @Parameter(description = "ID dell'utente", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Nuovo ruolo dell'utente",
                required = true,
                content = @Content(schema = @Schema(implementation = UpdateUserRoleRequest.class))
            )
            @RequestBody UpdateUserRoleRequest request) {
        try {
            User updatedUser = userService.updateUserRole(id, request.getRole());
            return ResponseEntity.ok(new UserDTO(updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}