package com.gestioneEventi.dto.ferie;

import java.time.LocalDate;

import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.dto.event.EventDTO;
import com.gestioneEventi.models.Ferie;
import com.gestioneEventi.models.Status;

import lombok.Data;

@Data
public class FerieDTO {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private EventDTO event;
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