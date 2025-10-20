package com.gestioneEventi.dto.ferie;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Richiesta di aggiornamento di una richiesta ferie esistente")
public class UpdateFerieRequest {
    
    @Schema(description = "Nuovo titolo della richiesta ferie", example = "Ferie invernali")
    private String title;
    
    @Schema(description = "Nuova data di inizio", example = "2025-12-20")
    private LocalDate startDate;
    
    @Schema(description = "Nuova data di fine", example = "2025-12-27")
    private LocalDate endDate;
}