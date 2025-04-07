package com.example.application.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
public class Calendar extends AbstractEntity {

    @NotNull
    private String calendarName;

    private int monthsInYear;

    @ElementCollection
    private List<String> monthNames;

    private int daysInMonth;

    private int daysInWeek;

    @ElementCollection
    private List<String> weekdayNames;

    private int moonCount;

    @OneToMany(mappedBy = "calendar")
    private List<Moon> moons;

    @Embedded
    private CalendarDate currentDate;

    private int currentYearStartDay;

    @Lob
    private String donjonJson;

    // Getters & Setters

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public int getMonthsInYear() {
        return monthsInYear;
    }

    public void setMonthsInYear(int monthsInYear) {
        this.monthsInYear = monthsInYear;
    }

    public List<String> getMonthNames() {
        return monthNames;
    }

    public void setMonthNames(List<String> monthNames) {
        this.monthNames = monthNames;
    }

    public int getDaysInMonth() {
        return daysInMonth;
    }

    public void setDaysInMonth(int daysInMonth) {
        this.daysInMonth = daysInMonth;
    }

    public int getDaysInWeek() {
        return daysInWeek;
    }

    public void setDaysInWeek(int daysInWeek) {
        this.daysInWeek = daysInWeek;
    }

    public List<String> getWeekdayNames() {
        return weekdayNames;
    }

    public void setWeekdayNames(List<String> weekdayNames) {
        this.weekdayNames = weekdayNames;
    }

    public List<Moon> getMoons() {
        return moons;
    }

    public void setMoons(List<Moon> moons) {
        this.moons = moons;
    }

    public int getMoonCount() {
        return moonCount;
    }

    public void setMoonCount(int moonCount) {
        this.moonCount = moonCount;
    }

    public CalendarDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(CalendarDate currentDate) {
        this.currentDate = currentDate;
    }

    public int getCurrentYearStartDay() {
        return currentYearStartDay;
    }

    public void setCurrentYearStartDay(int currentYearStartWeekday) {
        this.currentYearStartDay = currentYearStartWeekday;
    }
}
