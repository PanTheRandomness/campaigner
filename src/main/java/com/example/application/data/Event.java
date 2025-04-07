package com.example.application.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Entity
public class Event extends AbstractEntity {

    @NotNull
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "eventtype_id")
    @ColumnDefault("1")
    private EventType eventType;

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

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public EventDuration getDuration() {
        return duration;
    }

    public void setDuration(EventDuration duration) {
        this.duration = duration;
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
