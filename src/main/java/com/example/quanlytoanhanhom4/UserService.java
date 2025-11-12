package com.example.quanlytoanhanhom4;

import java.sql.*;

public class UserService {

    /**
     * Verify login credentials
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return Role của user nếu đăng nhập thành công, null nếu sai
     */
    public static String verifyLogin(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT role, password FROM user WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String role = rs.getString("role");

                // Kiểm tra nếu password đã được hash (SHA-256 hash có 64 ký tự)
                // Hoặc verify với PasswordUtils
                if (storedPassword != null) {
                    // Nếu password có độ dài < 64, có thể là plaintext (backward compatibility)
                    // Nhưng ưu tiên verify với hash trước
                    if (PasswordUtils.verifyPassword(password, storedPassword)) {
                        return role;
                    }
                    // Fallback: nếu verify hash thất bại và password ngắn, thử so sánh plaintext
                    // (Chỉ để tương thích với dữ liệu cũ, nên migrate sang hash sau)
                    if (storedPassword.length() < 64 && password.equals(storedPassword)) {
                        // TODO: Nên hash lại password này và update vào database
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
