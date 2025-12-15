package com.example.quanlytoanhanhom4.ui.auth;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.util.PasswordUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterForm extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Đăng ký tài khoản");

        // ===== Title =====
        Label title = new Label("Đăng ký tài khoản");
        title.setStyle("""
        -fx-font-size: 22px;
        -fx-font-weight: bold;
        -fx-text-fill: #0A4D8C;
    """);

        // ===== Labels =====
        Label userLabel = new Label("Tên đăng nhập");
        Label passLabel = new Label("Mật khẩu");
        Label phoneLabel = new Label("Số điện thoại");
        Label emailLabel = new Label("Email");

        // ===== Input fields =====
        TextField userField = new TextField();
        PasswordField passField = new PasswordField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();

        String inputStyle = """
        -fx-background-radius: 8;
        -fx-border-radius: 8;
        -fx-border-color: #90CAF9;
        -fx-padding: 8;
        -fx-font-size: 13px;
    """;

        userField.setStyle(inputStyle);
        passField.setStyle(inputStyle);
        phoneField.setStyle(inputStyle);
        emailField.setStyle(inputStyle);

        userField.setPromptText("Nhập tên đăng nhập");
        passField.setPromptText("Nhập mật khẩu");
        phoneField.setPromptText("Nhập số điện thoại");
        emailField.setPromptText("Nhập email");

        // ===== Message =====
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-font-size: 13px;");

        // ===== Button =====
        Button registerButton = new Button("Đăng ký");
        registerButton.setStyle("""
        -fx-background-color: linear-gradient(#2196F3, #1976D2);
        -fx-text-fill: white;
        -fx-font-size: 14px;
        -fx-font-weight: bold;
        -fx-background-radius: 10;
        -fx-padding: 10 20;
        -fx-cursor: hand;
    """);

        registerButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();

            if (registerUser(username, password, phone, email)) {
                messageLabel.setStyle("-fx-text-fill: #2E7D32;");
                messageLabel.setText("✔ Đăng ký thành công!");
            } else {
                messageLabel.setStyle("-fx-text-fill: #C62828;");
                messageLabel.setText("✘ Tên đăng nhập đã tồn tại!");
            }
        });

        // ===== Form layout =====
        VBox form = new VBox(8,
                title,
                userLabel, userField,
                passLabel, passField,
                phoneLabel, phoneField,
                emailLabel, emailField,
                registerButton,
                messageLabel
        );

        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(25));
        form.setStyle("""
        -fx-background-color: white;
        -fx-background-radius: 15;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);
    """);

        // ===== Root =====
        StackPane root = new StackPane(form);
        root.setStyle("-fx-background-color: #E3F2FD;");

        Scene scene = new Scene(root, 380, 480);
        stage.setScene(scene);
        stage.show();
    }


    private boolean registerUser(String username, String password, String phone, String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT * FROM user WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false;
            }

            String insertSql = "INSERT INTO user(username, role, password, phone_number, email) VALUES(?, 'resident', ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, PasswordUtils.hashPassword(password));
            insertStmt.setString(3, phone);
            insertStmt.setString(4, email);
            insertStmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}


