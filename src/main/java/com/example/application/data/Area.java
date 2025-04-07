package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
public class Area extends AbstractEntity {
    @NotNull
    private String areaName;
    private String areaDescription;
    private String areaHistory;
    private boolean privateArea;

    //TODO: Add Area World & relation

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
