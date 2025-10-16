package com.gestioneEventi.dto;

import com.gestioneEventi.models.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Richiesta di aggiornamento del ruolo di un utente")
public class UpdateUserRoleRequest {
    
    @Schema(
        description = "Nuovo ruolo dell'utente", 
        example = "EDITOR",
        allowableValues = {"USER", "EDITOR", "ADMIN"},
        required = true
    )
    private Role role;
}