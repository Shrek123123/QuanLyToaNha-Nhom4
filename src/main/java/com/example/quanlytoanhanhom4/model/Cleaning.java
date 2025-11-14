package com.example.quanlytoanhanhom4.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cleaning {
    private int id;
    private String area; // LOBY, HALLWAY, RESTROOM, PARKING, GARDEN, etc.
    private String cleaningType; // DAILY, WEEKLY, DEEP_CLEAN, SPECIAL
    private LocalDate scheduledDate;
    private LocalDate completedDate;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private Integer assignedTo;
    private Integer createdBy;
    private LocalDateTime createdDate;
    private String notes;
    private Integer qualityRating; // 1-5

    public Cleaning() {
    }

    public Cleaning(String area, String cleaningType, LocalDate scheduledDate) {
        this.area = area;
        this.cleaningType = cleaningType;
        this.scheduledDate = scheduledDate;
        this.status = "PENDING";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCleaningType() {
        return cleaningType;
    }

    public void setCleaningType(String cleaningType) {
        this.cleaningType = cleaningType;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(Integer qualityRating) {
        this.qualityRating = qualityRating;
    }
}






