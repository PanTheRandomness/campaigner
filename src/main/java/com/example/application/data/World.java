package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
public class World extends AbstractEntity {

    @NotNull
    private String worldName;

    private String worldDescription;

    private String worldHistory;
    //TODO: Add Areas-relation

    // Getters & Setters

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getWorldHistory() {
        return worldHistory;
    }

    public void setWorldHistory(String worldHistory) {
        this.worldHistory = worldHistory;
    }

    public String getWorldDescription() {
        return worldDescription;
    }

    public void setWorldDescription(String worldDescription) {
        this.worldDescription = worldDescription;
    }
}
