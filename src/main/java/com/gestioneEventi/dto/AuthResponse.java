package com.gestioneEventi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Risposta di autenticazione contenente il token JWT e i dati utente")
public class AuthResponse {
    
    @Schema(description = "Token JWT per l'autenticazione alle API", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "Dati dell'utente autenticato")
    private UserDTO user;
}