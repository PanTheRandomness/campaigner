package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
public class Place extends AbstractEntity {
    @NotNull
    private String placeName;
    private String placeDescription;
    //TODO: Add Area & relation
    private String placeHistory;
    private boolean privatePlace;

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
}
