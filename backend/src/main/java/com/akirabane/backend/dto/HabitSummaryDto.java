package com.akirabane.backend.dto;

public class HabitSummaryDto {

    private Long id;
    private String name;
    private String category;
    private String frequencyType;
    private boolean archived;

    public HabitSummaryDto() {}

    public HabitSummaryDto(Long id, String name, String category, String frequencyType, boolean archived) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.frequencyType = frequencyType;
        this.archived = archived;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getFrequencyType() { return frequencyType; }
    public void setFrequencyType(String frequencyType) { this.frequencyType = frequencyType; }

    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
}