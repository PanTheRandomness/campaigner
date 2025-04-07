package com.example.application.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Moon extends AbstractEntity {

    @NotNull
    private String moonName;

    @NotNull
    private int cycle;

    @NotNull
    private int shift;

    @ManyToOne
    @JoinColumn(name="calendar_id")
    private Calendar calendar;

    // Getters & Setters

    public String getMoonName() {
        return moonName;
    }

    public void setMoonName(String moonName) {
        this.moonName = moonName;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getShift() {
        return shift;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
