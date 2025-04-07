package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class Place extends AbstractEntity {

    @NotNull
    private String placeName;

    private String placeDescription;

    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;

    private String placeHistory;

    private boolean privatePlace;

    @OneToMany(mappedBy = "place")
    private List<Event> events;

    // Getters & Setters

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getPlaceHistory() {
        return placeHistory;
    }

    public void setPlaceHistory(String placeHistory) {
        this.placeHistory = placeHistory;
    }

    public boolean isPrivatePlace() {
        return privatePlace;
    }

    public void setPrivatePlace(boolean privatePlace) {
        this.privatePlace = privatePlace;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
