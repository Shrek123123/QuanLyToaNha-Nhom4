package com.example.quanlytoanhanhom4.service.auth;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.util.PasswordUtils;
import com.example.quanlytoanhanhom4.util.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class UserService {

    private UserService() {
        // Utility class
    }

    public static String verifyLogin(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id, role, password FROM user WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Integer userId = rs.getInt("id");
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");

                if (storedPassword != null) {
                    if (PasswordUtils.verifyPassword(password, storedPassword)) {
                        UserSession.setUser(username, role, userId);
                        return role;
                    }
                    if (storedPassword.length() < 64 && password.equals(storedPassword)) {
                        UserSession.setUser(username, role, userId);
                        return role;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Lỗi khi verify login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}


