package com.example.quanlytoanhanhom4;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {
    private static Stage primaryStage;
    private static String currentRole;

    public static void show(Stage stage, String role) {
        primaryStage = stage;
        currentRole = role;

        BorderPane root = new BorderPane();

        // Header
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: #2874A6;");

        Label welcomeLabel = new Label("Chào mừng đến hệ thống Quản Lý Tòa Nhà");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label roleLabel = new Label("Vai trò: " + role.toUpperCase());
        roleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        header.getChildren().addAll(welcomeLabel, roleLabel);
        root.setTop(header);

        // Content area
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: #f5f5f5;");

        // Menu buttons based on role
        if ("admin".equalsIgnoreCase(role)) {
            // Admin menu
            Label adminLabel = new Label("Chức năng quản trị:");
            adminLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            Button manageUsersBtn = new Button("Quản lý người dùng");
            Button manageApartmentsBtn = new Button("Quản lý căn hộ");
            Button manageComplaintsBtn = new Button("Quản lý khiếu nại");
            Button manageVehiclesBtn = new Button("Quản lý xe");

            styleButton(manageUsersBtn);
            styleButton(manageApartmentsBtn);
            styleButton(manageComplaintsBtn);
            styleButton(manageVehiclesBtn);

            // TODO: Implement action handlers
            manageUsersBtn.setOnAction(e -> showMessage("Chức năng đang phát triển"));
            manageApartmentsBtn.setOnAction(e -> showMessage("Chức năng đang phát triển"));
            manageComplaintsBtn.setOnAction(e -> showMessage("Chức năng đang phát triển"));
            manageVehiclesBtn.setOnAction(e -> showMessage("Chức năng đang phát triển"));

            content.getChildren().addAll(adminLabel, manageUsersBtn,
                    manageApartmentsBtn, manageComplaintsBtn, manageVehiclesBtn);

        } else if ("resident".equalsIgnoreCase(role) || "user".equalsIgnoreCase(role)) {
            // Resident/User menu
            Label residentLabel = new Label("Chức năng cư dân:");
            residentLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            Button myApartmentBtn = new Button("Thông tin căn hộ");
            Button submitComplaintBtn = new Button("Gửi khiếu nại");
            Button myVehiclesBtn = new Button("Quản lý xe");
            Button viewComplaintsBtn = new Button("Xem khiếu nại");

            styleButton(myApartmentBtn);
            styleButton(submitComplaintBtn);
            styleButton(myVehiclesBtn);
            styleButton(viewComplaintsBtn);

            // TODO: Implement action handlers
            myApartmentBtn.setOnAction(e -> showMessage("Chức năng đang phát triển"));
            submitComplaintBtn.setOnAction(e -> showMessage("Chức năng đang phát triển"));
            myVehiclesBtn.setOnAction(e -> showMessage("Chức năng đang phát triển"));
            viewComplaintsBtn.setOnAction(e -> showMessage("Chức năng đang phát triển"));

            content.getChildren().addAll(residentLabel, myApartmentBtn,
                    submitComplaintBtn, myVehiclesBtn, viewComplaintsBtn);
        } else {
            // Default menu
            Label defaultLabel = new Label("Chào mừng! Vai trò: " + role);
            defaultLabel.setStyle("-fx-font-size: 18px;");
            content.getChildren().add(defaultLabel);
        }

        root.setCenter(content);

        // Footer with logout button
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: #e0e0e0;");

        Button logoutBtn = new Button("Đăng xuất");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 20;");
        logoutBtn.setOnAction(e -> {
            // Close dashboard and show login again
            primaryStage.close();
            try {
                new HelloApplication().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        footer.getChildren().add(logoutBtn);
        root.setBottom(footer);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Dashboard - Quản Lý Tòa Nhà");
        primaryStage.show();
    }

    private static void styleButton(Button button) {
        button.setPrefWidth(250);
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

    private static void showMessage(String message) {
        Stage messageStage = new Stage();
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Label label = new Label(message);
        Button okBtn = new Button("OK");
        okBtn.setOnAction(e -> messageStage.close());

        vbox.getChildren().addAll(label, okBtn);
        Scene scene = new Scene(vbox, 300, 150);
        messageStage.setScene(scene);
        messageStage.setTitle("Thông báo");
        messageStage.show();
    }
}
