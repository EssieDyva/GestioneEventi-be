package com.gestioneEventi.dto.teambuilding;

import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.dto.activity.ActivityDTO;
import com.gestioneEventi.dto.event.EventDTO;
import com.gestioneEventi.models.TeamBuildingPartecipation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Schema(description = "Dettagli di una partecipazione a un evento TEAM_BUILDING")
public class TeamBuildingPartecipationDTO {

    @Schema(description = "ID univoco della partecipazione", example = "1")
    private Long id;

    @Schema(description = "Utente partecipante")
    private UserDTO user;

    @Schema(description = "Evento associato")
    private EventDTO event;

    @Schema(description = "Attività scelte per la partecipazione")
    private List<ActivityDTO> chosenActivities;

    @Schema(description = "Giorno di inizio", example = "1")
    private Integer dayStart;

    @Schema(description = "Giorno di fine", example = "2")
    private Integer dayEnd;

    public TeamBuildingPartecipationDTO(TeamBuildingPartecipation partecipation) {
        this.id = partecipation.getId();
        this.user = new UserDTO(partecipation.getUser());
        this.event = new EventDTO(partecipation.getEvent());
        this.chosenActivities = partecipation.getChosenActivities()
                .stream()
                .map(ActivityDTO::new)
                .collect(Collectors.toList());
        this.dayStart = partecipation.getDayStart();
        this.dayEnd = partecipation.getDayEnd();
    }
}
