package com.gestioneEventi.dto;

import com.gestioneEventi.models.Role;
import com.gestioneEventi.models.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private String group;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole() != null ? user.getRole() : null;
        this.group = user.getGroup() != null ? user.getGroup().getName() : null;
    }
}
