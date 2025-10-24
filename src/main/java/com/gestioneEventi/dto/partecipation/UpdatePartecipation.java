package com.gestioneEventi.dto.partecipation;

import com.gestioneEventi.models.PartecipationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Richiesta di aggiornamento dello stato di partecipazione ad un evento")
public class UpdatePartecipation {
    
    @Schema(
        description = "Nuovo stato di accettazione dell'evento (true = accettato, false = rifiutato, null = in sospeso)", 
        example = "true",
        nullable = true
    )
    private PartecipationStatus status;
}