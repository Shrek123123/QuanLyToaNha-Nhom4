package com.example.quanlytoanhanhom4;

import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginForm extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Đăng nhập hệ thống");

        Label title = new Label("Đăng nhập hệ thống");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label userLabel = new Label("Tên đăng nhập:");
        TextField userField = new TextField();
        Label passLabel = new Label("Mật khẩu:");
        PasswordField passField = new PasswordField();

        Button loginButton = new Button("Đăng nhập");
        Button registerButton = new Button("Đăng ký");
        Label messageLabel = new Label();

        HBox buttonBox = new HBox(10, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(12, title, userLabel, userField, passLabel, passField, buttonBox, messageLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));

        // Xử lý đăng nhập
        loginButton.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("⚠️ Vui lòng nhập đủ thông tin!");
                return;
            }

            String role = UserService.verifyLogin(username, password);
            if (role != null) {
                messageLabel.setText("✅ Đăng nhập thành công! (Vai trò: " + role + ")");
                primaryStage.close();
                Stage dashboardStage = new Stage();
                Dashboard.show(dashboardStage, role);
            } else {
                messageLabel.setText("❌ Sai tên đăng nhập hoặc mật khẩu!");
            }
        });

        // Chuyển sang form đăng ký
        registerButton.setOnAction(e -> {
            RegisterForm registerForm = new RegisterForm();
            try {
                registerForm.start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(layout, 340, 320);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
