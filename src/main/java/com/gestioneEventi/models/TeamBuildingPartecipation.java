package com.gestioneEventi.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamBuildingPartecipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private Event event;

    @ManyToMany
    @JoinTable(name = "teambuilding_partecipation_activities", joinColumns = @JoinColumn(name = "partecipation_id"), inverseJoinColumns = @JoinColumn(name = "activity_id"))
    private Set<Activity> chosenActivities = new HashSet<>();

    private Integer dayStart;
    private Integer dayEnd;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TeamBuildingPartecipation))
            return false;
        TeamBuildingPartecipation other = (TeamBuildingPartecipation) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}