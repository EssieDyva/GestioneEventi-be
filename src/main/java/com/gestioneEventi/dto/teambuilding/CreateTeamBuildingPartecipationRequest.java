package com.gestioneEventi.dto.teambuilding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Schema(description = "Richiesta di partecipazione ad un evento TEAM_BUILDING")
public class CreateTeamBuildingPartecipationRequest {

    @Schema(description = "Lista delle attività scelte", example = "[1, 2]")
    private Set<Long> activityIds;

    @Schema(description = "Data di inizio partecipazione", example = "2025-12-01", required = true)
    private LocalDate startDate;

    @Schema(description = "Data di fine partecipazione", example = "2025-12-04", required = true)
    private LocalDate endDate;
}
