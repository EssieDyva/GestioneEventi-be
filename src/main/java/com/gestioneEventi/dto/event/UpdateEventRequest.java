package com.gestioneEventi.dto.event;

import java.time.LocalDate;
import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Richiesta di aggiornamento di un evento esistente")
public class UpdateEventRequest {
    
    @Schema(description = "Nuovo titolo dell'evento", example = "Team Building Autunnale")
    private String title;
    
    @Schema(description = "Nuova data di inizio", example = "2025-09-10")
    private LocalDate startDate;
    
    @Schema(description = "Nuova data di fine", example = "2025-09-15")
    private LocalDate endDate;
    
    @Schema(description = "Nuova lista degli ID degli utenti invitati", example = "[1, 3, 5, 7]")
    private Set<Long> invitedUserIds;
}