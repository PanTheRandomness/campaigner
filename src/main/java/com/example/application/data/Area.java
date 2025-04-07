package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Area extends AbstractEntity {

    private String areaName;

    private String areaDescription;

    private String areaHistory;

    @ManyToOne
    @JoinColumn(name = "world_id")
    private World world;

    private boolean privateArea;

    @OneToMany(mappedBy = "area")
    private List<Place> places;

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

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public boolean isPrivateArea() {
        return privateArea;
    }

    public void setPrivateArea(boolean privateArea) {
        this.privateArea = privateArea;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
