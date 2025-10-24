package com.gestioneEventi.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @ManyToOne
    private User createdBy;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "event_users", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> invitedUsers = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Activity> activities = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Event))
            return false;
        Event other = (Event) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}