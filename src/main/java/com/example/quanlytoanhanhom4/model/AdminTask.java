package com.example.quanlytoanhanhom4.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminTask {
    private int id;
    private String taskType; // HR, FINANCE, DOCUMENT, MEETING, OTHER
    private String title;
    private String description;
    private Integer assignedTo;
    private Integer createdBy;
    private LocalDateTime createdDate;
    private LocalDate dueDate;
    private LocalDate completedDate;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String priority; // LOW, MEDIUM, HIGH, URGENT
    private String notes;

    public AdminTask() {
    }

    public AdminTask(String taskType, String title, String description, LocalDate dueDate, String priority) {
        this.taskType = taskType;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = "PENDING";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}






