package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class World extends AbstractEntity {

    @NotNull
    private String worldName;

    private String worldDescription;

    private String worldHistory;

    @OneToMany(mappedBy = "world")
    private List<Area> areas;

    //TODO: Add Campaign-relation

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
