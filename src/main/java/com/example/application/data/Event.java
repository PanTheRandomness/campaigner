package com.example.application.data;

import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class Event extends AbstractEntity {

    private String name;
    private String description;
    private String type;
    private LocalDate time;
    private String location;
    private boolean reoccurring;
    private boolean private_;

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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public LocalDate getTime() {
        return time;
    }
    public void setTime(LocalDate time) {
        this.time = time;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public boolean isReoccurring() {
        return reoccurring;
    }
    public void setReoccurring(boolean reoccurring) {
        this.reoccurring = reoccurring;
    }
    public boolean isPrivate_() {
        return private_;
    }
    public void setPrivate_(boolean private_) {
        this.private_ = private_;
    }

}
