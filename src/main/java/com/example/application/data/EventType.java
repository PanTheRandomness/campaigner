package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
public class EventType extends AbstractEntity {
    @NotNull
    private String eventType;
    @NotNull
    private String eventColour;

    //TODO: Add Event-relation (M2M)

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventColour() {
        return eventColour;
    }

    public void setEventColour(String eventColour) {
        this.eventColour = eventColour;
    }
}
