package com.gestioneEventi.dto.ferie;

import java.time.LocalDate;

import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.dto.event.EventDTO;
import com.gestioneEventi.models.Ferie;
import com.gestioneEventi.models.Status;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dettagli completi di una richiesta ferie")
public class FerieDTO {
    
    @Schema(description = "ID univoco della richiesta ferie", example = "1")
    private Long id;
    
    @Schema(description = "Titolo della richiesta ferie", example = "Ferie estive")
    private String title;
    
    @Schema(description = "Data di inizio delle ferie", example = "2025-07-16")
    private LocalDate startDate;
    
    @Schema(description = "Data di fine delle ferie", example = "2025-07-19")
    private LocalDate endDate;
    
    @Schema(description = "Stato della richiesta ferie", example = "APPROVED")
    private Status status;
    
    @Schema(description = "Evento associato alla richiesta ferie")
    private EventDTO event;
    
    @Schema(description = "Utente che ha creato la richiesta ferie")
    private UserDTO createdBy;

    public FerieDTO(Ferie ferie) {
        this.id = ferie.getId();
        this.title = ferie.getTitle();
        this.startDate = ferie.getStartDate();
        this.endDate = ferie.getEndDate();
        this.status = ferie.getStatus();
        this.event = ferie.getEvent() != null ? new EventDTO(ferie.getEvent()) : null;
        this.createdBy = ferie.getCreatedBy() != null ? new UserDTO(ferie.getCreatedBy()) : null;
    }
}