package com.gestioneEventi.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.transaction.Status;
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
    private Status status; // INVITED, ACCEPTED, REJECTED
    private LocalDate chosenStart;
    private LocalDate chosenEnd;
}