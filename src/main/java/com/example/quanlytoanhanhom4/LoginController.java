package com.example.quanlytoanhanhom4;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label statusLabel;

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("⚠️ Vui lòng nhập đầy đủ thông tin!");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        // Sử dụng UserService để verify login
        String role = UserService.verifyLogin(username, password);
        if (role != null) {
            statusLabel.setText("✅ Đăng nhập thành công!");
            statusLabel.setStyle("-fx-text-fill: green;");
            openDashboard(role);
        } else {
            statusLabel.setText("❌ Sai tên đăng nhập hoặc mật khẩu!");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    protected void handleRegister() {
        // Mở form đăng ký
        RegisterForm registerForm = new RegisterForm();
        try {
            registerForm.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Lỗi khi mở form đăng ký!");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void openDashboard(String role) {
        // Lấy Stage hiện tại và đóng login window
        Stage loginStage = (Stage) usernameField.getScene().getWindow();
        loginStage.close();

        // Mở Dashboard
        Stage dashboardStage = new Stage();
        com.example.quanlytoanhanhom4.Dashboard.show(dashboardStage, role != null ? role : "user");
    }
}
