package com.gestioneEventi.models;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enumeration representing the possible statuses of a user's participation in an event.
 * Used to track whether users have accepted, rejected, or are still considering event invitations.
 *
 */
@Schema(description = "Stati possibili per una partecipazione", allowableValues = {"PENDING", "ACCEPTED", "REJECTED"})
public enum PartecipationStatus {

    /**
     * Participation is pending user confirmation.
     * The user has been invited but hasn't responded yet.
     */
    @Schema(description = "Partecipazione in attesa di conferma")
    PENDING,

    /**
     * Participation has been accepted by the user.
     * The user has confirmed they will attend the event.
     */
    @Schema(description = "Partecipazione accettata")
    ACCEPTED,

    /**
     * Participation has been rejected by the user.
     * The user has declined the invitation to the event.
     */
    @Schema(description = "Partecipazione rifiutata")
    REJECTED
}