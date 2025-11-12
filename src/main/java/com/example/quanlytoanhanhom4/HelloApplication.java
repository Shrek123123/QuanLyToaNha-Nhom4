package com.example.quanlytoanhanhom4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Đảm bảo database đã được khởi tạo
        DatabaseInitializer.ensureInitialized();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/quanlytoanhanhom4/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 350);
        stage.setTitle("Đăng nhập quản lý toà nhà");
        stage.setScene(scene);
        stage.setResizable(false); // Không cho phép thay đổi kích thước
        stage.show();
    }

    public static void main(String[] args) {
        // Khởi tạo database trước khi chạy app
        DatabaseInitializer.initialize();
        launch();
    }
}
