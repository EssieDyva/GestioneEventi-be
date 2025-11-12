package com.gestioneEventi.dto.event;

import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.models.Event;
import com.gestioneEventi.models.EventType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Schema(description = "Dettagli completi di un evento")
public class EventDTO {
    
    @Schema(description = "ID univoco dell'evento", example = "1")
    private Long id;
    
    @Schema(description = "Titolo dell'evento", example = "Team Building Estivo")
    private String title;
    
    @Schema(description = "Data di inizio dell'evento", example = "2025-07-15")
    private LocalDate startDate;
    
    @Schema(description = "Data di fine dell'evento", example = "2025-07-20")
    private LocalDate endDate;

    @Schema(description = "Data di inizio confermata (solo per TEAM_BUILDING)", example = "2025-07-16")
    private LocalDate confirmedStartDate;

    @Schema(description = "Data di fine confermata (solo per TEAM_BUILDING)", example = "2025-07-18")
    private LocalDate confirmedEndDate;

    @Schema(description = "ID dell'attività confermata (solo per TEAM_BUILDING)", example = "2")
    private Long confirmedActivityId;

    @Schema(description = "Tipo di evento", example = "TEAM_BUILDING")
    private EventType eventType;
    
    @Schema(description = "Utente che ha creato l'evento")
    private UserDTO createdBy;
    
    @Schema(description = "Lista degli utenti invitati all'evento")
    private List<UserDTO> invitedUsers;

    public EventDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.confirmedStartDate = event.getConfirmedStartDate();
        this.confirmedEndDate = event.getConfirmedEndDate();
        this.confirmedActivityId = event.getConfirmedActivityId();
        this.eventType = event.getEventType();
        this.createdBy = event.getCreatedBy() != null ? new UserDTO(event.getCreatedBy()) : null;
        this.invitedUsers = event.getInvitedUsers().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
}
