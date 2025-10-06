package com.gestioneEventi.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.gestioneEventi.models.Role;
import com.gestioneEventi.models.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
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
