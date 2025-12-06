package com.akirabane.backend.dto;

import java.util.List;

public class UserWithHabitsDto {

    private Long id;
    private String fullName;
    private String email;
    private List<HabitSummaryDto> habits;

    public UserWithHabitsDto() {}

    public UserWithHabitsDto(Long id, String fullName, String email, List<HabitSummaryDto> habits) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.habits = habits;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<HabitSummaryDto> getHabits() { return habits; }
    public void setHabits(List<HabitSummaryDto> habits) { this.habits = habits; }
}