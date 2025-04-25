package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class EventType extends AbstractEntity {

    @NotNull
    private String eventTypeName;

    @NotNull
    private String eventColour;

    @OneToMany(mappedBy = "type")
    private List<Event> events;

    //Getters & Setters

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventType) {
        this.eventTypeName = eventType;
    }

    public String getEventColour() {
        return eventColour;
    }

    public void setEventColour(String eventColour) {
        this.eventColour = eventColour;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
