package com.akirabane.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HabitRequestDto {

    @NotBlank(message = "must not be blank")
    @Size(max = 100, message = "length must be <= 100")
    private String name;

    @Size(max = 50, message = "length must be <= 50")
    private String category;

    @NotBlank(message = "must not be blank")
    @Size(max = 30, message = "length must be <= 30")
    private String frequencyType;

    private boolean archived;

    public HabitRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(String frequencyType) {
        this.frequencyType = frequencyType;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
