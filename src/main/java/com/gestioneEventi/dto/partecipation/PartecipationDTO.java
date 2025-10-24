package com.gestioneEventi.dto.partecipation;

import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.dto.event.EventDTO;
import com.gestioneEventi.models.Partecipation;
import com.gestioneEventi.models.PartecipationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dettagli di una partecipazione ad un evento")
public class PartecipationDTO {
    
    @Schema(description = "ID univoco della partecipazione", example = "1")
    private Long id;
    
    @Schema(
        description = "Stato di accettazione dell'evento da parte dell'utente (PENDING = in sospeso, ACCEPTED = accettato, REJECTED = rifiutato)",
        example = "ACCEPTED",
        allowableValues = {"PENDING", "ACCEPTED", "REJECTED"}
    )
    private PartecipationStatus status;
    
    @Schema(description = "Evento associato alla partecipazione")
    private EventDTO event;
    
    @Schema(description = "Utente partecipante")
    private UserDTO user;

    public PartecipationDTO(Partecipation partecipation) {
        this.id = partecipation.getId();
        this.status = partecipation.getStatus();
        this.event = partecipation.getEvent() != null ? new EventDTO(partecipation.getEvent()) : null;
        this.user = partecipation.getUser() != null ? new UserDTO(partecipation.getUser()) : null;
    }
}
