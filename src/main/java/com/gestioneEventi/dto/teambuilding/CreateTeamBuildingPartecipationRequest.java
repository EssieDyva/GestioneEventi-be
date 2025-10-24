package com.gestioneEventi.dto.teambuilding;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "Richiesta di partecipazione ad un evento TEAM_BUILDING")
public class CreateTeamBuildingPartecipationRequest {

    @Schema(description = "Lista delle attività scelte", example = "[1, 2]")
    private Set<Long> activityIds;

    @Schema(description = "Giorno di inizio (facoltativo)", example = "1")
    private Integer dayStart;

    @Schema(description = "Giorno di fine (facoltativo)", example = "2")
    private Integer dayEnd;
}