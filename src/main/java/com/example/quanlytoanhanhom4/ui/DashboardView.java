package com.example.quanlytoanhanhom4.ui;

import com.example.quanlytoanhanhom4.util.UserSession;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class DashboardView {
    private static Stage primaryStage;
    private static String currentRole;

    private DashboardView() {
        // Utility class
    }

    public static void show(Stage stage, String role) {
        primaryStage = stage;
        currentRole = role;

        BorderPane root = new BorderPane();

        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2874A6;");

        Label welcomeLabel = new Label("Hệ thống Quản lý Kỹ thuật Tòa Nhà");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label roleLabel = new Label("Vai trò: " + role.toUpperCase());
        roleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        header.getChildren().addAll(welcomeLabel, roleLabel);
        root.setTop(header);

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: #f5f5f5;");

        Label technicalLabel = new Label("Quản lý Hệ thống Kỹ thuật:");
        technicalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button bmsBtn = new Button("Giám sát & Điều khiển (BMS)");
        Button maintenanceBtn = new Button("Bảo trì & Bảo dưỡng");
        Button securityBtn = new Button("Quản lý An ninh");
        Button cleaningBtn = new Button("Quản lý Vệ sinh");
        Button adminBtn = new Button("Quản lý Hành chính & Nhân sự");
        Button customerBtn = new Button("Quản lý Khách hàng");

        styleButton(bmsBtn);
        styleButton(maintenanceBtn);
        styleButton(securityBtn);
        styleButton(cleaningBtn);
        styleButton(adminBtn);
        styleButton(customerBtn);

        bmsBtn.setOnAction(e -> openModule("/com/example/quanlytoanhanhom4/fxml/bms.fxml", "Giám sát & Điều khiển BMS"));
        maintenanceBtn.setOnAction(e -> openModule("/com/example/quanlytoanhanhom4/fxml/maintenance.fxml", "Bảo trì & Bảo dưỡng"));
        securityBtn.setOnAction(e -> openModule("/com/example/quanlytoanhanhom4/fxml/security.fxml", "Quản lý An ninh"));
        cleaningBtn.setOnAction(e -> openModule("/com/example/quanlytoanhanhom4/fxml/cleaning.fxml", "Quản lý Vệ sinh"));
        adminBtn.setOnAction(e -> openModule("/com/example/quanlytoanhanhom4/fxml/admin.fxml", "Quản lý Hành chính & Nhân sự"));
        customerBtn.setOnAction(e -> openModule("/com/example/quanlytoanhanhom4/fxml/customer.fxml", "Quản lý Khách hàng"));

        content.getChildren().addAll(technicalLabel, bmsBtn, maintenanceBtn, securityBtn, cleaningBtn, adminBtn, customerBtn);
        root.setCenter(content);

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: #e0e0e0;");

        Button backBtn = new Button("← Quay lại");
        backBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 8 20; -fx-font-weight: bold; -fx-background-radius: 5;");
        backBtn.setOnAction(e -> handleBack());

        Button logoutBtn = new Button("Đăng xuất");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 20; -fx-font-weight: bold; -fx-background-radius: 5;");
        logoutBtn.setOnAction(e -> handleLogout());

        footer.getChildren().addAll(backBtn, logoutBtn);
        root.setBottom(footer);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dashboard - Quản lý Kỹ thuật Tòa Nhà");
        primaryStage.show();
    }

    private static void styleButton(Button button) {
        button.setPrefWidth(300);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: #2874A6; -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-background-radius: 5;");
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: #1a5a8a; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-background-radius: 5;"));
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: #2874A6; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-font-weight: bold; " +
                        "-fx-background-radius: 5;"));
    }

    private static void openModule(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(DashboardView.class.getResource(fxmlPath));
            Stage moduleStage = new Stage();
            Scene scene = new Scene(loader.load(), 1000, 600);
            moduleStage.setTitle(title);
            moduleStage.setScene(scene);
            moduleStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Lỗi khi mở module: " + e.getMessage());
        }
    }

    private static void showMessage(String message) {
        Stage messageStage = new Stage();
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Label label = new Label(message);
        Button okBtn = new Button("OK");
        okBtn.setOnAction(e -> messageStage.close());

        vbox.getChildren().addAll(label, okBtn);
        Scene scene = new Scene(vbox, 350, 150);
        messageStage.setScene(scene);
        messageStage.setTitle("Thông báo");
        messageStage.show();
    }

    private static void handleBack() {
        try {
            if (primaryStage != null) {
                // Quay lại màn hình chính (main.fxml)
                FXMLLoader loader = new FXMLLoader(DashboardView.class.getResource("/com/example/quanlytoanhanhom4/fxml/main.fxml"));
                Scene scene = new Scene(loader.load(), 1080, 640);
                primaryStage.setTitle("Quản lý kỹ thuật tòa nhà");
                primaryStage.setScene(scene);
                primaryStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Không thể quay lại màn hình chính: " + e.getMessage());
        }
    }

    private static void handleLogout() {
        try {
            if (primaryStage != null) {
                primaryStage.close();
            }
            UserSession.clear();

            FXMLLoader loader = new FXMLLoader(DashboardView.class.getResource("/com/example/quanlytoanhanhom4/fxml/login.fxml"));
            Stage loginStage = new Stage();
            Scene scene = new Scene(loader.load(), 400, 350);
            loginStage.setTitle("Đăng nhập quản lý toà nhà");
            loginStage.setResizable(false);
            loginStage.setScene(scene);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Không thể quay lại màn hình đăng nhập: " + e.getMessage());
        }
    }
}


