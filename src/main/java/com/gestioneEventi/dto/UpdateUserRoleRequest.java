package com.gestioneEventi.dto;

import com.gestioneEventi.models.Role;

import lombok.Data;

@Data
public class UpdateUserRoleRequest {
    private Role role;
}