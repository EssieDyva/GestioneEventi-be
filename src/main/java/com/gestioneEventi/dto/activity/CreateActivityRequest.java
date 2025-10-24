package com.gestioneEventi.dto.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Richiesta per creare una nuova attività per un evento di tipo TEAM_BUILDING")
public class CreateActivityRequest {

    @Schema(description = "Nome dell'attività", example = "Gara di orienteering")
    private String name;

    @Schema(description = "Descrizione dell'attività", example = "Squadre che si sfidano in una gara di orientamento nel bosco")
    private String description;
}