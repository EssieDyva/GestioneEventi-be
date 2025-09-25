package com.gestioneEventi.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Event event;
    
    private String employeeEmail;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    private LocalDate chosenStart;
    private LocalDate chosenEnd;
}