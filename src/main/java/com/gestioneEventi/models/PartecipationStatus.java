package com.gestioneEventi.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Stati possibili per una partecipazione", allowableValues = {"PENDING", "ACCEPTED", "REJECTED"})
public enum PartecipationStatus {
    @Schema(description = "Partecipazione in attesa di conferma")
    PENDING,
    @Schema(description = "Partecipazione accettata")
    ACCEPTED,
    @Schema(description = "Partecipazione rifiutata")
    REJECTED
}
