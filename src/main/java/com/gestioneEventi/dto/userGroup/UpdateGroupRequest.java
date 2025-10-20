package com.gestioneEventi.dto.userGroup;

import java.util.List;

import lombok.Data;

@Data
public class UpdateGroupRequest {
    private String groupName;
    private List<String> memberEmails;
}
