package com.akirabane.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "habit_checks",
        uniqueConstraints = @UniqueConstraint(columnNames = {"habit_id", "check_date"}))
public class HabitCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "check_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean success = true;

    public HabitCheck() {}

    public HabitCheck(Habit habit, LocalDate date) {
        this.habit = habit;
        this.date = date;
        this.success = true;
    }

    // Getters / setters

    public Long getId() {
        return id;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
