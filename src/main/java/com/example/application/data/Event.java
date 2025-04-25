package com.example.application.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.util.Set;

@Entity
public class Event extends AbstractEntity {

    @NotNull
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name = "eventtype_id")
    @ColumnDefault("1")
    private EventType type;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "event_duration_id")
    private EventDuration duration;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Enumerated(EnumType.STRING)
    private ReoccurrenceType reoccurring; // Tyyppi muutettu

    private boolean privateEvent; // Nimi muutettu

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

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType eventType) {
        this.type = eventType;
    }

    public EventDuration getDuration() {
        return duration;
    }

    public void setDuration(EventDuration duration) {
        this.duration = duration;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public ReoccurrenceType getReoccurring() {
        return reoccurring;
    }

    public void setReoccurring(ReoccurrenceType reoccurring) {
        this.reoccurring = reoccurring;
    }

    public boolean isPrivateEvent() {
        return privateEvent;
    }

    public void setPrivateEvent(boolean privateEvent) {
        this.privateEvent = privateEvent;
    }

}
