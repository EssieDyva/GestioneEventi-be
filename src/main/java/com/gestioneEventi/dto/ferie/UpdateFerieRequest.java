package com.gestioneEventi.dto.ferie;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UpdateFerieRequest {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
}