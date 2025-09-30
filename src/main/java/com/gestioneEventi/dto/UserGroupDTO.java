package com.gestioneEventi.dto;

import com.gestioneEventi.models.UserGroup;
import lombok.Data;

@Data
public class UserGroupDTO {
    private Long id;
    private String name;

    public UserGroupDTO(UserGroup group) {
        this.id = group.getId();
        this.name = group.getName();
    }
}
