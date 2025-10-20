package com.gestioneEventi.dto.partecipation;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Richiesta di creazione di partecipazioni ad un evento")
public class CreatePartecipation {
    
    @Schema(description = "ID dell'evento", example = "1", required = true)
    private Long eventId;
    
    @Schema(
        description = "Lista degli ID degli utenti partecipanti", 
        example = "[1, 2, 3]",
        required = true
    )
    private List<Long> userIds;
}