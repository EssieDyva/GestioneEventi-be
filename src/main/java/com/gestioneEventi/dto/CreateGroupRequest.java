package com.gestioneEventi.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Richiesta di creazione di un nuovo gruppo")
public class CreateGroupRequest {
    
    @Schema(description = "Nome del gruppo", example = "Team Sviluppo", required = true)
    private String groupName;
    
    @Schema(
        description = "Lista delle email dei membri del gruppo", 
        example = "[\"mario.rossi@example.com\", \"lucia.bianchi@example.com\"]",
        required = true
    )
    private List<String> memberEmails;
}