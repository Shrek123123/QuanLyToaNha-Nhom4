package com.example.quanlytoanhanhom4.service;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.model.Security;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SecurityService {
    
    public static List<Security> getAllIncidents() {
        List<Security> incidents = new ArrayList<>();
        String sql = "SELECT * FROM security ORDER BY reported_date DESC, priority DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Security security = mapResultSetToSecurity(rs);
                incidents.add(security);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidents;
    }
    
    public static List<Security> getIncidentsByStatus(String status) {
        List<Security> incidents = new ArrayList<>();
        String sql = "SELECT * FROM security WHERE status = ? ORDER BY reported_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Security security = mapResultSetToSecurity(rs);
                incidents.add(security);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incidents;
    }
    
    public static boolean addIncident(Security security) {
        String sql = "INSERT INTO security (incident_type, location, description, reported_by, status, " +
                     "assigned_to, priority) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, security.getIncidentType());
            pstmt.setString(2, security.getLocation());
            pstmt.setString(3, security.getDescription());
            pstmt.setObject(4, security.getReportedBy());
            pstmt.setString(5, security.getStatus() != null ? security.getStatus() : "OPEN");
            pstmt.setObject(6, security.getAssignedTo());
            pstmt.setString(7, security.getPriority() != null ? security.getPriority() : "MEDIUM");
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateIncident(Security security) {
        String sql = "UPDATE security SET incident_type = ?, location = ?, description = ?, status = ?, " +
                     "assigned_to = ?, resolved_date = ?, resolution = ?, priority = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, security.getIncidentType());
            pstmt.setString(2, security.getLocation());
            pstmt.setString(3, security.getDescription());
            pstmt.setString(4, security.getStatus());
            pstmt.setObject(5, security.getAssignedTo());
            pstmt.setObject(6, security.getResolvedDate());
            pstmt.setString(7, security.getResolution());
            pstmt.setString(8, security.getPriority());
            pstmt.setInt(9, security.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean resolveIncident(int id, String resolution) {
        String sql = "UPDATE security SET status = 'RESOLVED', resolved_date = NOW(), resolution = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resolution);
            pstmt.setInt(2, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteIncident(int id) {
        String sql = "DELETE FROM security WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static Security mapResultSetToSecurity(ResultSet rs) throws SQLException {
        Security security = new Security();
        security.setId(rs.getInt("id"));
        security.setIncidentType(rs.getString("incident_type"));
        security.setLocation(rs.getString("location"));
        security.setDescription(rs.getString("description"));
        security.setReportedBy(rs.getObject("reported_by", Integer.class));
        security.setStatus(rs.getString("status"));
        security.setAssignedTo(rs.getObject("assigned_to", Integer.class));
        security.setResolution(rs.getString("resolution"));
        security.setPriority(rs.getString("priority"));
        
        Timestamp reportedDate = rs.getTimestamp("reported_date");
        if (reportedDate != null) {
            security.setReportedDate(reportedDate.toLocalDateTime());
        }
        
        Timestamp resolvedDate = rs.getTimestamp("resolved_date");
        if (resolvedDate != null) {
            security.setResolvedDate(resolvedDate.toLocalDateTime());
        }
        
        return security;
    }
}




