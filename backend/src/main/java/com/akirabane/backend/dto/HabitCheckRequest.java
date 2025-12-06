package com.akirabane.backend.dto;

import java.time.LocalDate;

public class HabitCheckRequest {
    private LocalDate date;

    public HabitCheckRequest() {}

    public HabitCheckRequest(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
