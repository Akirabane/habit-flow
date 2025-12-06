package com.akirabane.backend.dto;

import java.time.LocalDate;

public class HabitCheckRequestDto {
    private LocalDate date;

    public HabitCheckRequestDto() {}

    public HabitCheckRequestDto(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
