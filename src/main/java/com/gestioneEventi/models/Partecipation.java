package com.gestioneEventi.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Schema(description = "Modello di partecipazione di un utente a un evento")
public class Partecipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID univoco della partecipazione", example = "1")
    private Long id;

    @Schema(
        description = "Stato della partecipazione (PENDING = in sospeso, ACCEPTED = accettato, REJECTED = rifiutato)",
        example = "PENDING",
        allowableValues = {"PENDING", "ACCEPTED", "REJECTED"}
    )
    private PartecipationStatus status;

    @ManyToOne
    @Schema(description = "Evento associato alla partecipazione")
    private Event event;

    @ManyToOne
    @Schema(description = "Utente partecipante")
    private User user;
}
