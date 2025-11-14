package com.example.quanlytoanhanhom4.model;

import java.time.LocalDateTime;

public class BMSSystem {
    private int id;
    private String systemType; // ĐIỆN, NƯỚC, HVAC, PCCC, AN_NINH, CHIEU_SANG
    private String systemName;
    private String location;
    private String status; // NORMAL, WARNING, ERROR, MAINTENANCE
    private Double currentValue;
    private String unit;
    private LocalDateTime lastUpdated;
    private String description;

    public BMSSystem() {
    }

    public BMSSystem(String systemType, String systemName, String location, String status, 
                     Double currentValue, String unit, String description) {
        this.systemType = systemType;
        this.systemName = systemName;
        this.location = location;
        this.status = status;
        this.currentValue = currentValue;
        this.unit = unit;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}






