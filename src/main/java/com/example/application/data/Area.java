package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Area extends AbstractEntity {

    private String areaName;

    private String areaDescription;

    private String areaHistory;

    @ManyToOne
    @JoinColumn(name = "world_id")
    private World world;

    private boolean privateArea;

    //TODO: Add Place-relation

    // Getters & Setters

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaDescription() {
        return areaDescription;
    }

    public void setAreaDescription(String areaDescription) {
        this.areaDescription = areaDescription;
    }

    public String getAreaHistory() {
        return areaHistory;
    }

    public void setAreaHistory(String areaHistory) {
        this.areaHistory = areaHistory;
    }

    public boolean isPrivateArea() {
        return privateArea;
    }

    public void setPrivateArea(boolean privateArea) {
        this.privateArea = privateArea;
    }
}
