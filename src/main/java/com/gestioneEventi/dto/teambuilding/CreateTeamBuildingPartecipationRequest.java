package com.gestioneEventi.dto.teambuilding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Richiesta di partecipazione ad un evento TEAM_BUILDING")
public class CreateTeamBuildingPartecipationRequest {

    @Schema(description = "ID dell'attività scelta", example = "1", required = true)
    private Long activityId;

    @Schema(description = "Data di inizio partecipazione", example = "2025-12-01", required = true)
    private LocalDate startDate;

    @Schema(description = "Data di fine partecipazione", example = "2025-12-04", required = true)
    private LocalDate endDate;
}