package com.example.quanlytoanhanhom4.service;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.model.CustomerRequest;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerRequestService {
    
    public static List<CustomerRequest> getAllRequests() {
        List<CustomerRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM customer_request ORDER BY created_date DESC, priority DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                CustomerRequest request = mapResultSetToCustomerRequest(rs);
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public static List<CustomerRequest> getRequestsByStatus(String status) {
        List<CustomerRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM customer_request WHERE status = ? ORDER BY created_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CustomerRequest request = mapResultSetToCustomerRequest(rs);
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public static List<CustomerRequest> getRequestsByResident(int residentId) {
        List<CustomerRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM customer_request WHERE resident_id = ? ORDER BY created_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, residentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CustomerRequest request = mapResultSetToCustomerRequest(rs);
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public static boolean addRequest(CustomerRequest request) {
        String sql = "INSERT INTO customer_request (resident_id, request_type, title, content, status, " +
                     "priority, assigned_to) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, request.getResidentId());
            pstmt.setString(2, request.getRequestType());
            pstmt.setString(3, request.getTitle());
            pstmt.setString(4, request.getContent());
            pstmt.setString(5, request.getStatus() != null ? request.getStatus() : "PENDING");
            pstmt.setString(6, request.getPriority() != null ? request.getPriority() : "MEDIUM");
            pstmt.setObject(7, request.getAssignedTo());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateRequest(CustomerRequest request) {
        String sql = "UPDATE customer_request SET request_type = ?, title = ?, content = ?, status = ?, " +
                     "priority = ?, resolved_date = ?, assigned_to = ?, resolution = ?, satisfaction_rating = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, request.getRequestType());
            pstmt.setString(2, request.getTitle());
            pstmt.setString(3, request.getContent());
            pstmt.setString(4, request.getStatus());
            pstmt.setString(5, request.getPriority());
            pstmt.setObject(6, request.getResolvedDate());
            pstmt.setObject(7, request.getAssignedTo());
            pstmt.setString(8, request.getResolution());
            pstmt.setObject(9, request.getSatisfactionRating());
            pstmt.setInt(10, request.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean resolveRequest(int id, String resolution, Integer satisfactionRating) {
        String sql = "UPDATE customer_request SET status = 'RESOLVED', resolved_date = NOW(), resolution = ?, satisfaction_rating = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resolution);
            pstmt.setObject(2, satisfactionRating);
            pstmt.setInt(3, id);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteRequest(int id) {
        String sql = "DELETE FROM customer_request WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static CustomerRequest mapResultSetToCustomerRequest(ResultSet rs) throws SQLException {
        CustomerRequest request = new CustomerRequest();
        request.setId(rs.getInt("id"));
        request.setResidentId(rs.getInt("resident_id"));
        request.setRequestType(rs.getString("request_type"));
        request.setTitle(rs.getString("title"));
        request.setContent(rs.getString("content"));
        request.setStatus(rs.getString("status"));
        request.setPriority(rs.getString("priority"));
        request.setAssignedTo(rs.getObject("assigned_to", Integer.class));
        request.setResolution(rs.getString("resolution"));
        request.setSatisfactionRating(rs.getObject("satisfaction_rating", Integer.class));
        
        Timestamp createdDate = rs.getTimestamp("created_date");
        if (createdDate != null) {
            request.setCreatedDate(createdDate.toLocalDateTime());
        }
        
        Timestamp resolvedDate = rs.getTimestamp("resolved_date");
        if (resolvedDate != null) {
            request.setResolvedDate(resolvedDate.toLocalDateTime());
        }
        
        return request;
    }
}




