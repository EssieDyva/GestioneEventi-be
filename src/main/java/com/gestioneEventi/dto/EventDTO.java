package com.gestioneEventi.dto;

import com.gestioneEventi.models.Event;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class EventDTO {
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private UserDTO createdBy;
    private List<UserGroupDTO> invitedGroups;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.createdBy = event.getCreatedBy() != null ? new UserDTO(event.getCreatedBy()) : null;
        this.invitedGroups = event.getInvitedGroups().stream()
                .map(UserGroupDTO::new)
                .collect(Collectors.toList());
    }
}