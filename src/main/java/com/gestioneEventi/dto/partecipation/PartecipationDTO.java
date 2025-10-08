package com.gestioneEventi.dto.partecipation;

import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.dto.event.EventDTO;
import com.gestioneEventi.models.Partecipation;

import lombok.Data;

@Data
public class PartecipationDTO {
    private Long id;
    private Boolean isEventAccepted;
    private EventDTO event;
    private UserDTO user;

    public PartecipationDTO(Partecipation partecipation) {
        this.id = partecipation.getId();
        this.isEventAccepted = partecipation.getIsEventAccepted();
        this.event = partecipation.getEvent() != null ? new EventDTO(partecipation.getEvent()) : null;
        this.user = partecipation.getUser() != null ? new UserDTO(partecipation.getUser()) : null;
    }
}
