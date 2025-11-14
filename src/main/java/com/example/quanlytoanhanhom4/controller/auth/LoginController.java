package com.example.quanlytoanhanhom4.controller.auth;

import com.example.quanlytoanhanhom4.service.auth.UserService;
import com.example.quanlytoanhanhom4.ui.auth.RegisterForm;
import com.example.quanlytoanhanhom4.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
            statusLabel.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        String role = UserService.verifyLogin(username, password);
        if (role != null) {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("✅ Đăng nhập thành công!");
            openMainView();
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("❌ Sai tên đăng nhập hoặc mật khẩu!");
        }
    }

    @FXML
    protected void handleRegister() {
        try {
            RegisterForm registerForm = new RegisterForm();
            registerForm.start(new Stage());
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Không thể mở màn hình đăng ký: " + e.getMessage());
        }
    }

    private void openMainView() {
        try {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/quanlytoanhanhom4/fxml/main.fxml"));
            Scene scene = new Scene(loader.load(), 1080, 640);
            currentStage.setTitle("Quản lý kỹ thuật tòa nhà");
            currentStage.setScene(scene);
            currentStage.show();
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Không thể tải giao diện chính: " + e.getMessage());
            e.printStackTrace();
            UserSession.clear();
        }
    }
}


