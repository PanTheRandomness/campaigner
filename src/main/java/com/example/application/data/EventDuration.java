package com.example.application.data;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class EventDuration extends AbstractEntity {

    @NotNull
    private CalendarDate startDate;

    @Nullable
    private CalendarDate endDate;

    private int duration;

    @OneToOne(mappedBy = "duration")
    private Event event;

    //Getters & Setters

    public CalendarDate getStartDate() {
        return startDate;
    }

    public void setStartDate(CalendarDate startDate) {
        this.startDate = startDate;
    }

    public CalendarDate getEndDate() {
        return endDate;
    }

    public void setEndDate(CalendarDate endDate) {
        this.endDate = endDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(CalendarDate start, @Nullable CalendarDate end, Calendar calendar) {
        int calculatedDuration;

        if (end == null) {
            calculatedDuration = calculateDuration(start, calendar.getCurrentDate(), calendar);
        } else {
            calculatedDuration = calculateDuration(start, end, calendar);
        }

        this.duration = calculatedDuration;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    // Methods

    public int calculateDuration(CalendarDate start, CalendarDate end, Calendar calendar) {
        int daysBetween = 0;

        int startYear = start.getYear();
        int startMonth = start.getMonth();
        int startDay = start.getDay();

        int endYear = end.getYear();
        int endMonth = end.getMonth();
        int endDay = end.getDay();

        while (startYear < endYear || (startYear == endYear && startMonth < endMonth) ||
                (startYear == endYear && startMonth == endMonth && startDay < endDay)) {

            startDay++;

            if (startDay > calendar.getDaysInMonth()) {
                startDay = 1;
                startMonth++;
            }

            if (startMonth > calendar.getMonthsInYear()) {
                startMonth = 1;
                startYear++;
            }
            daysBetween++;
        }
        return daysBetween;
    }
}
