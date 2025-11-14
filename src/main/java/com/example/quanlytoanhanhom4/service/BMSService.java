package com.example.quanlytoanhanhom4.service;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.model.BMSSystem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BMSService {
    
    public static List<BMSSystem> getAllSystems() {
        List<BMSSystem> systems = new ArrayList<>();
        String sql = "SELECT * FROM bms_system ORDER BY system_type, system_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                BMSSystem system = mapResultSetToBMSSystem(rs);
                systems.add(system);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return systems;
    }
    
    public static List<BMSSystem> getSystemsByType(String systemType) {
        List<BMSSystem> systems = new ArrayList<>();
        String sql = "SELECT * FROM bms_system WHERE system_type = ? ORDER BY system_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, systemType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                BMSSystem system = mapResultSetToBMSSystem(rs);
                systems.add(system);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return systems;
    }
    
    public static boolean addSystem(BMSSystem system) {
        String sql = "INSERT INTO bms_system (system_type, system_name, location, status, current_value, unit, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, system.getSystemType());
            pstmt.setString(2, system.getSystemName());
            pstmt.setString(3, system.getLocation());
            pstmt.setString(4, system.getStatus() != null ? system.getStatus() : "NORMAL");
            pstmt.setObject(5, system.getCurrentValue());
            pstmt.setString(6, system.getUnit());
            pstmt.setString(7, system.getDescription());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateSystem(BMSSystem system) {
        String sql = "UPDATE bms_system SET system_type = ?, system_name = ?, location = ?, status = ?, " +
                     "current_value = ?, unit = ?, description = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, system.getSystemType());
            pstmt.setString(2, system.getSystemName());
            pstmt.setString(3, system.getLocation());
            pstmt.setString(4, system.getStatus());
            pstmt.setObject(5, system.getCurrentValue());
            pstmt.setString(6, system.getUnit());
            pstmt.setString(7, system.getDescription());
            pstmt.setInt(8, system.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateSystemStatus(int id, String status, Double value) {
        String sql = "UPDATE bms_system SET status = ?, current_value = ?, last_updated = NOW() WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setObject(2, value);
            pstmt.setInt(3, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteSystem(int id) {
        String sql = "DELETE FROM bms_system WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static BMSSystem mapResultSetToBMSSystem(ResultSet rs) throws SQLException {
        BMSSystem system = new BMSSystem();
        system.setId(rs.getInt("id"));
        system.setSystemType(rs.getString("system_type"));
        system.setSystemName(rs.getString("system_name"));
        system.setLocation(rs.getString("location"));
        system.setStatus(rs.getString("status"));
        system.setCurrentValue(rs.getObject("current_value", Double.class));
        system.setUnit(rs.getString("unit"));
        system.setDescription(rs.getString("description"));
        
        Timestamp timestamp = rs.getTimestamp("last_updated");
        if (timestamp != null) {
            system.setLastUpdated(timestamp.toLocalDateTime());
        }
        
        return system;
    }
}




