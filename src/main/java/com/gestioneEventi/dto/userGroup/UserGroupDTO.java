package com.gestioneEventi.dto.userGroup;

import java.util.Set;
import java.util.stream.Collectors;

import com.gestioneEventi.models.UserGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Dettagli completi di un gruppo utenti")
public class UserGroupDTO {
    
    @Schema(description = "ID univoco del gruppo", example = "1")
    private Long id;
    
    @Schema(description = "Nome del gruppo", example = "Team Sviluppo")
    private String name;
    
    @Schema(
        description = "Lista delle email dei membri del gruppo", 
        example = "[\"mario.rossi@example.com\", \"lucia.bianchi@example.com\"]"
    )
    private Set<String> members;

    public UserGroupDTO(UserGroup group) {
        this.id = group.getId();
        this.name = group.getName();
        this.members = group.getMembers() != null
                ? group.getMembers().stream()
                        .map(user -> user.getEmail())
                        .collect(Collectors.toSet())
                : Set.of();
    }
}