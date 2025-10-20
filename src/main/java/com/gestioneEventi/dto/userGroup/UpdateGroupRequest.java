package com.gestioneEventi.dto.userGroup;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Richiesta di aggiornamento di un gruppo esistente")
public class UpdateGroupRequest {
    
    @Schema(description = "Nuovo nome del gruppo", example = "Team Backend", required = true)
    private String groupName;
    
    @Schema(
        description = "Nuova lista delle email dei membri del gruppo", 
        example = "[\"mario.rossi@example.com\", \"lucia.bianchi@example.com\", \"paolo.verdi@example.com\"]",
        required = true
    )
    private List<String> memberEmails;
}