package com.gestioneEventi.dto.partecipation;

import lombok.Data;

@Data
public class CreatePartecipation {
    private Long id;
    private Boolean isEventAccepted;
    private Long eventId;
}