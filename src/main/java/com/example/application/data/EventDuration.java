package com.example.application.data;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class EventDuration extends AbstractEntity {

    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "year", column = @Column(name = "start_year")),
            @AttributeOverride(name = "month", column = @Column(name = "start_month")),
            @AttributeOverride(name = "day", column = @Column(name = "start_day"))
    })
    private CalendarDate startDate;

    @Nullable
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "year", column = @Column(name = "end_year")),
            @AttributeOverride(name = "month", column = @Column(name = "end_month")),
            @AttributeOverride(name = "day", column = @Column(name = "end_day"))
    })
    private CalendarDate endDate;

    private int duration;

    @OneToOne(mappedBy = "duration", cascade = CascadeType.ALL)
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

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    // Methods
    public int calculateDuration(CalendarDate start, @Nullable CalendarDate end, Calendar calendar) {
        CalendarDate effectiveEnd = (end != null) ? end : calendar.getCurrentDate();
        int daysBetween = 0;

        int startYear = start.getYear();
        int startMonth = start.getMonth();
        int startDay = start.getDay();

        int endYear = effectiveEnd.getYear();
        int endMonth = effectiveEnd.getMonth();
        int endDay = effectiveEnd.getDay();

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
