package com.gestioneEventi.dto.teambuilding;

import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.dto.event.EventDTO;
import com.gestioneEventi.models.TeamBuildingPartecipation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Dettagli di una partecipazione a un evento TEAM_BUILDING")
public class TeamBuildingPartecipationDTO {

    @Schema(description = "ID univoco della partecipazione", example = "1")
    private Long id;

    @Schema(description = "Utente partecipante")
    private UserDTO user;

    @Schema(description = "Evento associato")
    private EventDTO event;

    @Schema(description = "ID dell'attività scelta", example = "1")
    private Long activityId;

    @Schema(description = "Data di inizio partecipazione", example = "2025-12-01")
    private LocalDate startDate;

    @Schema(description = "Data di fine partecipazione", example = "2025-12-02")
    private LocalDate endDate;

    public TeamBuildingPartecipationDTO(TeamBuildingPartecipation partecipation) {
        this.id = partecipation.getId();
        this.user = new UserDTO(partecipation.getUser());
        this.event = new EventDTO(partecipation.getEvent());
        this.activityId = partecipation.getChosenActivityId();
        this.startDate = partecipation.getStartDate();
        this.endDate = partecipation.getEndDate();
    }
}