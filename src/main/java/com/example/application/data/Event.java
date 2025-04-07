package com.example.application.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class Event extends AbstractEntity {

    @NotNull
    private String name;

    private String description;

    //TODO: Add EventType

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_duration_id", referencedColumnName = "id")
    private EventDuration duration;

    //TODO: Add Place

    private boolean reoccurring;

    private boolean private_;

    // Getters & Setters

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isReoccurring() {
        return reoccurring;
    }
    public void setReoccurring(boolean reoccurring) {
        this.reoccurring = reoccurring;
    }
    public boolean isPrivate_() {
        return private_;
    }
    public void setPrivate_(boolean private_) {
        this.private_ = private_;
    }

}
