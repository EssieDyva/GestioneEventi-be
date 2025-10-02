package com.gestioneEventi.dto.ferie;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CreateFerieRequest {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long eventId;
}