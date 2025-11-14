package com.example.quanlytoanhanhom4.model;

import java.time.LocalDateTime;

public class Security {
    private int id;
    private String incidentType; // CAMERA, ACCESS_CONTROL, EMERGENCY, THEFT, OTHER
    private String location;
    private String description;
    private Integer reportedBy;
    private LocalDateTime reportedDate;
    private String status; // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    private Integer assignedTo;
    private LocalDateTime resolvedDate;
    private String resolution;
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    public Security() {
    }

    public Security(String incidentType, String location, String description, Integer reportedBy, String priority) {
        this.incidentType = incidentType;
        this.location = location;
        this.description = description;
        this.reportedBy = reportedBy;
        this.priority = priority;
        this.status = "OPEN";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(Integer reportedBy) {
        this.reportedBy = reportedBy;
    }

    public LocalDateTime getReportedDate() {
        return reportedDate;
    }

    public void setReportedDate(LocalDateTime reportedDate) {
        this.reportedDate = reportedDate;
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

    public LocalDateTime getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(LocalDateTime resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}






