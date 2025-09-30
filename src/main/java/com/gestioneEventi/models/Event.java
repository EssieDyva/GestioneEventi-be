package com.gestioneEventi.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    private User createdBy;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "event_groups", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<UserGroup> invitedGroups = new HashSet<>();
}