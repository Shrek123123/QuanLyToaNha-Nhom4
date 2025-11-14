package com.example.quanlytoanhanhom4.service;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.model.Maintenance;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceService {
    
    public static List<Maintenance> getAllMaintenances() {
        List<Maintenance> maintenances = new ArrayList<>();
        String sql = "SELECT * FROM maintenance ORDER BY scheduled_date DESC, priority DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Maintenance maintenance = mapResultSetToMaintenance(rs);
                maintenances.add(maintenance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maintenances;
    }
    
    public static List<Maintenance> getMaintenancesByStatus(String status) {
        List<Maintenance> maintenances = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE status = ? ORDER BY scheduled_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Maintenance maintenance = mapResultSetToMaintenance(rs);
                maintenances.add(maintenance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maintenances;
    }
    
    public static boolean addMaintenance(Maintenance maintenance) {
        String sql = "INSERT INTO maintenance (system_id, system_type, maintenance_type, description, " +
                     "scheduled_date, status, assigned_to, priority, created_by, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, maintenance.getSystemId());
            pstmt.setString(2, maintenance.getSystemType());
            pstmt.setString(3, maintenance.getMaintenanceType());
            pstmt.setString(4, maintenance.getDescription());
            pstmt.setObject(5, maintenance.getScheduledDate());
            pstmt.setString(6, maintenance.getStatus() != null ? maintenance.getStatus() : "PENDING");
            pstmt.setObject(7, maintenance.getAssignedTo());
            pstmt.setString(8, maintenance.getPriority() != null ? maintenance.getPriority() : "MEDIUM");
            pstmt.setObject(9, maintenance.getCreatedBy());
            pstmt.setString(10, maintenance.getNotes());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateMaintenance(Maintenance maintenance) {
        String sql = "UPDATE maintenance SET system_id = ?, system_type = ?, maintenance_type = ?, " +
                     "description = ?, scheduled_date = ?, completed_date = ?, status = ?, " +
                     "assigned_to = ?, priority = ?, notes = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, maintenance.getSystemId());
            pstmt.setString(2, maintenance.getSystemType());
            pstmt.setString(3, maintenance.getMaintenanceType());
            pstmt.setString(4, maintenance.getDescription());
            pstmt.setObject(5, maintenance.getScheduledDate());
            pstmt.setObject(6, maintenance.getCompletedDate());
            pstmt.setString(7, maintenance.getStatus());
            pstmt.setObject(8, maintenance.getAssignedTo());
            pstmt.setString(9, maintenance.getPriority());
            pstmt.setString(10, maintenance.getNotes());
            pstmt.setInt(11, maintenance.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean completeMaintenance(int id, LocalDate completedDate, String notes) {
        String sql = "UPDATE maintenance SET status = 'COMPLETED', completed_date = ?, notes = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, completedDate);
            pstmt.setString(2, notes);
            pstmt.setInt(3, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteMaintenance(int id) {
        String sql = "DELETE FROM maintenance WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static Maintenance mapResultSetToMaintenance(ResultSet rs) throws SQLException {
        Maintenance maintenance = new Maintenance();
        maintenance.setId(rs.getInt("id"));
        maintenance.setSystemId(rs.getObject("system_id", Integer.class));
        maintenance.setSystemType(rs.getString("system_type"));
        maintenance.setMaintenanceType(rs.getString("maintenance_type"));
        maintenance.setDescription(rs.getString("description"));
        
        Date scheduledDate = rs.getDate("scheduled_date");
        if (scheduledDate != null) {
            maintenance.setScheduledDate(scheduledDate.toLocalDate());
        }
        
        Date completedDate = rs.getDate("completed_date");
        if (completedDate != null) {
            maintenance.setCompletedDate(completedDate.toLocalDate());
        }
        
        maintenance.setStatus(rs.getString("status"));
        maintenance.setAssignedTo(rs.getObject("assigned_to", Integer.class));
        maintenance.setPriority(rs.getString("priority"));
        maintenance.setCreatedBy(rs.getObject("created_by", Integer.class));
        maintenance.setNotes(rs.getString("notes"));
        
        Timestamp createdDate = rs.getTimestamp("created_date");
        if (createdDate != null) {
            maintenance.setCreatedDate(createdDate.toLocalDateTime());
        }
        
        return maintenance;
    }
}




