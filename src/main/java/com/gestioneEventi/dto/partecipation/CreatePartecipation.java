package com.gestioneEventi.dto.partecipation;

import java.util.List;

import lombok.Data;

@Data
public class CreatePartecipation {
    private Long eventId;
    private List<Long> userIds;
}
