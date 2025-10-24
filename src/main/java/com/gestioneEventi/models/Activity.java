package com.gestioneEventi.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    private boolean isCustom;

    @ManyToOne(optional = false)
    private Event event;

    @ManyToOne(optional = false)
    private User createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Activity))
            return false;
        Activity other = (Activity) o;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}