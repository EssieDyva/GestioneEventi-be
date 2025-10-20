package com.gestioneEventi.dto.ferie;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Richiesta di creazione di una nuova richiesta ferie")
public class CreateFerieRequest {
    
    @Schema(description = "Titolo della richiesta ferie", example = "Ferie estive", required = true)
    private String title;
    
    @Schema(description = "Data di inizio delle ferie", example = "2025-07-16", required = true)
    private LocalDate startDate;
    
    @Schema(description = "Data di fine delle ferie", example = "2025-07-19", required = true)
    private LocalDate endDate;
    
    @Schema(description = "ID dell'evento associato", example = "1", required = true)
    private Long eventId;
}