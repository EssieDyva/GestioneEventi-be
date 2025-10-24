package com.gestioneEventi.dto.activity;

import com.gestioneEventi.models.Activity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dettagli di un'attività per eventi TEAM_BUILDING")
public class ActivityDTO {

    @Schema(description = "ID univoco dell'attività", example = "1")
    private Long id;

    @Schema(description = "Nome dell'attività", example = "Orienteering")
    private String name;

    @Schema(description = "Descrizione dell'attività", example = "Team challenge in the woods")
    private String description;

    @Schema(description = "Indica se l'attività è personalizzata dall'utente", example = "false")
    private boolean isCustom;

    public ActivityDTO(Activity activity) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        this.isCustom = activity.isCustom();
    }
}
