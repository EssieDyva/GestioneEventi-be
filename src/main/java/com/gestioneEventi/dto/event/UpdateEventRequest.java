package com.gestioneEventi.dto.event;

import java.time.LocalDate;
import java.util.Set;

import lombok.Data;

@Data
public class UpdateEventRequest {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Set<Long> invitedGroupIds;
}