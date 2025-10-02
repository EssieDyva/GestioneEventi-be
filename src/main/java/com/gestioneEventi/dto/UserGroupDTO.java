package com.gestioneEventi.dto;

import java.util.Set;

import com.gestioneEventi.models.User;
import com.gestioneEventi.models.UserGroup;
import lombok.Data;

@Data
public class UserGroupDTO {
    private Long id;
    private String name;
    private Set<User> members;

    public UserGroupDTO(UserGroup group) {
        this.id = group.getId();
        this.name = group.getName();
        this.members = group.getMembers();
    }
}
