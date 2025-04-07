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

    //Onko String tässä hyvä?
    private String currentDate;

    private int currentYear;

    private String currentYearStartWeekday;

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

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public String getCurrentYearStartWeekday() {
        return currentYearStartWeekday;
    }

    public void setCurrentYearStartWeekday(String currentYearStartWeekday) {
        this.currentYearStartWeekday = currentYearStartWeekday;
    }
}
