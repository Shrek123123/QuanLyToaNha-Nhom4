package com.example.quanlytoanhanhom4;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class RegisterForm extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Đăng ký tài khoản");

        Label userLabel = new Label("Tên đăng nhập:");
        TextField userField = new TextField();

        Label passLabel = new Label("Mật khẩu:");
        PasswordField passField = new PasswordField();

        Label roleLabel = new Label("Vai trò:");
        TextField roleField = new TextField();
        roleField.setPromptText("VD: admin, user...");

        Label phoneLabel = new Label("Số điện thoại:");
        TextField phoneField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Button registerButton = new Button("Đăng ký");
        Label messageLabel = new Label();

        registerButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText();
            String role = roleField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // Validation
            String validationError = validateInput(username, password, role, phone, email);
            if (validationError != null) {
                messageLabel.setText(validationError);
                messageLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            if (registerUser(username, password, role, phone, email)) {
                messageLabel.setText("✅ Đăng ký thành công!");
                messageLabel.setStyle("-fx-text-fill: green;");
                // Clear fields after successful registration
                userField.clear();
                passField.clear();
                roleField.clear();
                phoneField.clear();
                emailField.clear();
            } else {
                messageLabel.setText("❌ Lỗi: Tên người dùng đã tồn tại hoặc lỗi hệ thống!");
                messageLabel.setStyle("-fx-text-fill: red;");
            }
        });

        VBox layout = new VBox(10,
                userLabel, userField,
                passLabel, passField,
                roleLabel, roleField,
                phoneLabel, phoneField,
                emailLabel, emailField,
                registerButton, messageLabel
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 350, 450);
        stage.setScene(scene);
        stage.show();
    }

    private String validateInput(String username, String password, String role, String phone, String email) {
        if (username.isEmpty()) {
            return "⚠️ Vui lòng nhập tên đăng nhập!";
        }
        if (username.length() < 3) {
            return "⚠️ Tên đăng nhập phải có ít nhất 3 ký tự!";
        }
        if (password.isEmpty()) {
            return "⚠️ Vui lòng nhập mật khẩu!";
        }
        if (password.length() < 6) {
            return "⚠️ Mật khẩu phải có ít nhất 6 ký tự!";
        }
        if (role.isEmpty()) {
            return "⚠️ Vui lòng nhập vai trò!";
        }
        if (!phone.isEmpty() && !isValidPhone(phone)) {
            return "⚠️ Số điện thoại không hợp lệ! (VD: 0123456789)";
        }
        if (!email.isEmpty() && !isValidEmail(email)) {
            return "⚠️ Email không hợp lệ! (VD: example@email.com)";
        }
        return null;
    }

    private boolean isValidEmail(String email) {
        // Simple email validation
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidPhone(String phone) {
        // Vietnamese phone number validation (10-11 digits, may start with 0)
        return phone.matches("^[0-9]{10,11}$");
    }

    private boolean registerUser(String username, String password, String role, String phone, String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Kiểm tra trùng username
            String checkSql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // Username đã tồn tại
            }

            // Thêm user mới với password đã được hash
            String insertSql = "INSERT INTO user(username, role, password, phone_number, email) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, role);
            // Hash password trước khi lưu
            String passwordHash = PasswordUtils.hashPassword(password);
            insertStmt.setString(3, passwordHash);
            insertStmt.setString(4, phone.isEmpty() ? null : phone);
            insertStmt.setString(5, email.isEmpty() ? null : email);
            insertStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
