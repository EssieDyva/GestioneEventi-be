package com.gestioneEventi.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.gestioneEventi.models.Role;
import com.gestioneEventi.models.User;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Informazioni complete di un utente")
public class UserDTO {
    
    @Schema(description = "ID univoco dell'utente", example = "1")
    private Long id;
    
    @Schema(description = "Nome completo dell'utente", example = "Mario Rossi")
    private String name;
    
    @Schema(description = "Email dell'utente", example = "mario.rossi@example.com")
    private String email;
    
    @Schema(description = "Ruolo dell'utente nel sistema", example = "USER")
    private Role role;
    
    @Schema(
        description = "Lista dei nomi dei gruppi a cui l'utente appartiene", 
        example = "[\"Team Sviluppo\", \"Amministratori\"]"
    )
    private List<String> group;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.group = user.getGroups() != null
                ? user.getGroups().stream()
                        .map(group -> group.getName())
                        .collect(Collectors.toList())
                : List.of();
    }
}