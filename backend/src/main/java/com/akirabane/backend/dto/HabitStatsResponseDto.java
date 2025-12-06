package com.akirabane.backend.dto;

import java.time.LocalDate;

public class HabitStatsResponseDto {

    private Long habitId;
    private LocalDate startDate;
    private LocalDate endDate;

    private long totalDays;
    private long checkedDays;
    private double successRate;

    private int currentStreak;
    private int longestStreak;

    public HabitStatsResponseDto() {
    }

    public HabitStatsResponseDto(Long habitId, LocalDate startDate, LocalDate endDate) {
        this.habitId = habitId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getHabitId() {
        return habitId;
    }

    public void setHabitId(Long habitId) {
        this.habitId = habitId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public long getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(long totalDays) {
        this.totalDays = totalDays;
    }

    public long getCheckedDays() {
        return checkedDays;
    }

    public void setCheckedDays(long checkedDays) {
        this.checkedDays = checkedDays;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }
}
