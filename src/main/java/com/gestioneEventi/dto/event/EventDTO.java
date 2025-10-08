package com.gestioneEventi.dto.event;

import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.EventType;

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
    private EventType eventType;
    private UserDTO createdBy;
    private List<UserDTO> invitedUsers;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.eventType = event.getEventType();
        this.createdBy = event.getCreatedBy() != null ? new UserDTO(event.getCreatedBy()) : null;
        this.invitedUsers = event.getInvitedUsers().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
}