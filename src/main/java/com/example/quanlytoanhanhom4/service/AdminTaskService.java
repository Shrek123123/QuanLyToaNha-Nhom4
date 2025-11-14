package com.example.quanlytoanhanhom4.service;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.model.AdminTask;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminTaskService {
    
    public static List<AdminTask> getAllTasks() {
        List<AdminTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM admin_task ORDER BY due_date DESC, priority DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                AdminTask task = mapResultSetToAdminTask(rs);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    
    public static List<AdminTask> getTasksByStatus(String status) {
        List<AdminTask> tasks = new ArrayList<>();
        String sql = "SELECT * FROM admin_task WHERE status = ? ORDER BY due_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                AdminTask task = mapResultSetToAdminTask(rs);
                tasks.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tasks;
    }
    
    public static boolean addTask(AdminTask task) {
        String sql = "INSERT INTO admin_task (task_type, title, description, assigned_to, created_by, " +
                     "due_date, status, priority, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTaskType());
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setObject(4, task.getAssignedTo());
            pstmt.setObject(5, task.getCreatedBy());
            pstmt.setObject(6, task.getDueDate());
            pstmt.setString(7, task.getStatus() != null ? task.getStatus() : "PENDING");
            pstmt.setString(8, task.getPriority() != null ? task.getPriority() : "MEDIUM");
            pstmt.setString(9, task.getNotes());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateTask(AdminTask task) {
        String sql = "UPDATE admin_task SET task_type = ?, title = ?, description = ?, assigned_to = ?, " +
                     "due_date = ?, completed_date = ?, status = ?, priority = ?, notes = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTaskType());
            pstmt.setString(2, task.getTitle());
            pstmt.setString(3, task.getDescription());
            pstmt.setObject(4, task.getAssignedTo());
            pstmt.setObject(5, task.getDueDate());
            pstmt.setObject(6, task.getCompletedDate());
            pstmt.setString(7, task.getStatus());
            pstmt.setString(8, task.getPriority());
            pstmt.setString(9, task.getNotes());
            pstmt.setInt(10, task.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean completeTask(int id, LocalDate completedDate, String notes) {
        String sql = "UPDATE admin_task SET status = 'COMPLETED', completed_date = ?, notes = ? WHERE id = ?";
        
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
    
    public static boolean deleteTask(int id) {
        String sql = "DELETE FROM admin_task WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static AdminTask mapResultSetToAdminTask(ResultSet rs) throws SQLException {
        AdminTask task = new AdminTask();
        task.setId(rs.getInt("id"));
        task.setTaskType(rs.getString("task_type"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setAssignedTo(rs.getObject("assigned_to", Integer.class));
        task.setCreatedBy(rs.getObject("created_by", Integer.class));
        task.setStatus(rs.getString("status"));
        task.setPriority(rs.getString("priority"));
        task.setNotes(rs.getString("notes"));
        
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            task.setDueDate(dueDate.toLocalDate());
        }
        
        Date completedDate = rs.getDate("completed_date");
        if (completedDate != null) {
            task.setCompletedDate(completedDate.toLocalDate());
        }
        
        Timestamp createdDate = rs.getTimestamp("created_date");
        if (createdDate != null) {
            task.setCreatedDate(createdDate.toLocalDateTime());
        }
        
        return task;
    }
}




