package com.example.quanlytoanhanhom4.service;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.model.Cleaning;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CleaningService {
    
    public static List<Cleaning> getAllCleanings() {
        List<Cleaning> cleanings = new ArrayList<>();
        String sql = "SELECT * FROM cleaning ORDER BY scheduled_date DESC, status";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Cleaning cleaning = mapResultSetToCleaning(rs);
                cleanings.add(cleaning);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cleanings;
    }
    
    public static List<Cleaning> getCleaningsByStatus(String status) {
        List<Cleaning> cleanings = new ArrayList<>();
        String sql = "SELECT * FROM cleaning WHERE status = ? ORDER BY scheduled_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Cleaning cleaning = mapResultSetToCleaning(rs);
                cleanings.add(cleaning);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cleanings;
    }
    
    public static boolean addCleaning(Cleaning cleaning) {
        String sql = "INSERT INTO cleaning (area, cleaning_type, scheduled_date, status, assigned_to, " +
                     "created_by, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cleaning.getArea());
            pstmt.setString(2, cleaning.getCleaningType());
            pstmt.setObject(3, cleaning.getScheduledDate());
            pstmt.setString(4, cleaning.getStatus() != null ? cleaning.getStatus() : "PENDING");
            pstmt.setObject(5, cleaning.getAssignedTo());
            pstmt.setObject(6, cleaning.getCreatedBy());
            pstmt.setString(7, cleaning.getNotes());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateCleaning(Cleaning cleaning) {
        String sql = "UPDATE cleaning SET area = ?, cleaning_type = ?, scheduled_date = ?, " +
                     "completed_date = ?, status = ?, assigned_to = ?, notes = ?, quality_rating = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cleaning.getArea());
            pstmt.setString(2, cleaning.getCleaningType());
            pstmt.setObject(3, cleaning.getScheduledDate());
            pstmt.setObject(4, cleaning.getCompletedDate());
            pstmt.setString(5, cleaning.getStatus());
            pstmt.setObject(6, cleaning.getAssignedTo());
            pstmt.setString(7, cleaning.getNotes());
            pstmt.setObject(8, cleaning.getQualityRating());
            pstmt.setInt(9, cleaning.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean completeCleaning(int id, LocalDate completedDate, Integer qualityRating, String notes) {
        String sql = "UPDATE cleaning SET status = 'COMPLETED', completed_date = ?, quality_rating = ?, notes = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, completedDate);
            pstmt.setObject(2, qualityRating);
            pstmt.setString(3, notes);
            pstmt.setInt(4, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteCleaning(int id) {
        String sql = "DELETE FROM cleaning WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static Cleaning mapResultSetToCleaning(ResultSet rs) throws SQLException {
        Cleaning cleaning = new Cleaning();
        cleaning.setId(rs.getInt("id"));
        cleaning.setArea(rs.getString("area"));
        cleaning.setCleaningType(rs.getString("cleaning_type"));
        
        Date scheduledDate = rs.getDate("scheduled_date");
        if (scheduledDate != null) {
            cleaning.setScheduledDate(scheduledDate.toLocalDate());
        }
        
        Date completedDate = rs.getDate("completed_date");
        if (completedDate != null) {
            cleaning.setCompletedDate(completedDate.toLocalDate());
        }
        
        cleaning.setStatus(rs.getString("status"));
        cleaning.setAssignedTo(rs.getObject("assigned_to", Integer.class));
        cleaning.setCreatedBy(rs.getObject("created_by", Integer.class));
        cleaning.setNotes(rs.getString("notes"));
        cleaning.setQualityRating(rs.getObject("quality_rating", Integer.class));
        
        Timestamp createdDate = rs.getTimestamp("created_date");
        if (createdDate != null) {
            cleaning.setCreatedDate(createdDate.toLocalDateTime());
        }
        
        return cleaning;
    }
}




