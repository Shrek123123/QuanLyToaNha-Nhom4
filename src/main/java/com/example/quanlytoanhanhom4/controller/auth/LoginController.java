package com.example.quanlytoanhanhom4.controller.auth;

import com.example.quanlytoanhanhom4.service.auth.UserService;
import com.example.quanlytoanhanhom4.ui.BuildingLogo;
import com.example.quanlytoanhanhom4.ui.auth.RegisterForm;
import com.example.quanlytoanhanhom4.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox loginVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Thêm logo vào đầu VBox
        if (loginVBox != null) {
            BuildingLogo logo = new BuildingLogo(60, 60);
            StackPane logoContainer = new StackPane(logo);
            logoContainer.setPadding(new Insets(0, 0, 10, 0));
            logoContainer.setMaxWidth(70); // Giới hạn kích thước để không vỡ giao diện
            logoContainer.setMaxHeight(70);
            loginVBox.getChildren().add(0, logoContainer);
        }
    }

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        // Attempt to verify login (this sets UserSession inside UserService)
        String returnedRole = UserService.verifyLogin(username, password);

        // Read role from UserSession as requested
        String sessionRole = UserSession.getCurrentRole();

        if (returnedRole != null && sessionRole != null) {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("✅ Đăng nhập thành công!");

            // Route based on session role
            if ("admin".equalsIgnoreCase(sessionRole)) {
                openAdminView();
            } else if ("resident".equalsIgnoreCase(sessionRole)) {
                openMainView();
            } else {
                // Fallback: open main view for any other/unknown roles
                openMainView();
            }
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
            currentStage.setResizable(true);
            currentStage.setMaximized(true);
            currentStage.show();
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Không thể tải giao diện chính: " + e.getMessage());
            e.printStackTrace();
            UserSession.clear();
        }
    }
    private void openAdminView() {
        try {
            Stage currentStage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/quanlytoanhanhom4/fxml/admin_main.fxml"));
            Scene scene = new Scene(loader.load(), 1080, 640);
            currentStage.setTitle("Quản lý kỹ thuật tòa nhà");
            currentStage.setScene(scene);
            currentStage.setResizable(true);
            currentStage.setMaximized(true);
            currentStage.show();
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Không thể tải giao diện chính: " + e.getMessage());
            e.printStackTrace();
            UserSession.clear();
        }
    }
}
