package com.example.quanlytoanhanhom4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class quản lý kết nối database
 * Lưu ý: Đảm bảo DatabaseInitializer.initialize() được gọi trước khi sử dụng
 */
public class DatabaseConnection {
    private static final String DB_NAME = "quanlytoanha";
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static boolean driverLoaded = false;

    /**
     * Lấy connection tới database
     * @return Connection object
     * @throws SQLException nếu không thể kết nối
     */
    public static Connection getConnection() throws SQLException {
        // Đảm bảo database đã được khởi tạo
        // (DatabaseInitializer.initialize() nên được gọi trong main)

        if (!driverLoaded) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                throw new SQLException("Không tìm thấy MySQL Driver! Vui lòng kiểm tra classpath.", e);
            }
        }

        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        if (conn == null) {
            throw new SQLException("Không thể kết nối tới database. " +
                    "Đảm bảo MySQL đang chạy và database '" + DB_NAME + "' đã được tạo.");
        }
        return conn;
    }

    /**
     * Test connection
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            return false;
        }
    }
}
