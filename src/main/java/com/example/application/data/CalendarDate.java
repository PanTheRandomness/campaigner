package com.example.application.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CalendarDate {

    @Column(insertable = false, updatable = false)
    private int year;
    @Column(insertable = false, updatable = false)
    private int month;
    @Column(insertable = false, updatable = false)
    private int day;

    public CalendarDate() {
    }

    public CalendarDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return year + "-" + month + "-" + day;
    }
}