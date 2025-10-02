package com.gestioneEventi.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.gestioneEventi.models.UserGroup;
import lombok.Data;

@Data
public class UserGroupDTO {
    private Long id;
    private String name;
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
