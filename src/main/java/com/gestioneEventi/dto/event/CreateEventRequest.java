package com.gestioneEventi.dto.event;

import java.time.LocalDate;
import java.util.Set;

import com.gestioneEventi.models.EventType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Richiesta di creazione di un nuovo evento")
public class CreateEventRequest {
    
    @Schema(description = "Titolo dell'evento", example = "Team Building Estivo", required = true)
    private String title;
    
    @Schema(description = "Data di inizio dell'evento", example = "2025-07-15", required = true)
    private LocalDate startDate;
    
    @Schema(description = "Data di fine dell'evento", example = "2025-07-20", required = true)
    private LocalDate endDate;
    
    @Schema(description = "Tipo di evento", example = "TEAM_BUILDING", required = true)
    private EventType eventType;
    
    @Schema(description = "Lista degli ID degli utenti invitati", example = "[1, 2, 3]")
    private Set<Long> invitedUserIds;
}