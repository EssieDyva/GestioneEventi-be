package com.gestioneEventi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Risposta di errore standardizzata")
public class ErrorResponse {
    
    @Schema(description = "Timestamp dell'errore", example = "2025-10-20T14:30:00")
    private LocalDateTime timestamp;
    
    @Schema(description = "Codice di stato HTTP", example = "404")
    private int status;
    
    @Schema(description = "Tipo di errore", example = "Not Found")
    private String error;
    
    @Schema(description = "Messaggio descrittivo dell'errore", example = "Evento non trovato con id: 123")
    private String message;
    
    @Schema(description = "Path della richiesta che ha generato l'errore", example = "/api/events/123")
    private String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}