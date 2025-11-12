package com.example.quanlytoanhanhom4;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        // Khởi tạo database trước khi chạy app
        DatabaseInitializer.initialize();
        Application.launch(HelloApplication.class, args);
    }
}
