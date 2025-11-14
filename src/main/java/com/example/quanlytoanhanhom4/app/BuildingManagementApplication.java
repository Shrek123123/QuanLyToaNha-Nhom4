<<<<<<<< HEAD:src/main/java/com/example/quanlytoanhanhom4/HelloApplication.java
package com.example.quanlytoanhanhom4;
========
package com.example.quanlytoanhanhom4.app;
>>>>>>>> 3dc00b0b8bc3172ff301f5b903156592332a3887:src/main/java/com/example/quanlytoanhanhom4/app/BuildingManagementApplication.java

import com.example.quanlytoanhanhom4.config.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BuildingManagementApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
<<<<<<<< HEAD:src/main/java/com/example/quanlytoanhanhom4/HelloApplication.java
        // Đảm bảo database đã được khởi tạo
        DatabaseInitializer.ensureInitialized();

========
>>>>>>>> 3dc00b0b8bc3172ff301f5b903156592332a3887:src/main/java/com/example/quanlytoanhanhom4/app/BuildingManagementApplication.java
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/quanlytoanhanhom4/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 350);
        stage.setTitle("Đăng nhập quản lý toà nhà");
        stage.setScene(scene);
<<<<<<<< HEAD:src/main/java/com/example/quanlytoanhanhom4/HelloApplication.java
        stage.setResizable(false); // Không cho phép thay đổi kích thước
========
        stage.setResizable(false);
>>>>>>>> 3dc00b0b8bc3172ff301f5b903156592332a3887:src/main/java/com/example/quanlytoanhanhom4/app/BuildingManagementApplication.java
        stage.show();
    }

    public static void main(String[] args) {
<<<<<<<< HEAD:src/main/java/com/example/quanlytoanhanhom4/HelloApplication.java
        // Khởi tạo database trước khi chạy app
        DatabaseInitializer.initialize();
        launch();
========
        DatabaseInitializer.initialize();
        launch(args);
>>>>>>>> 3dc00b0b8bc3172ff301f5b903156592332a3887:src/main/java/com/example/quanlytoanhanhom4/app/BuildingManagementApplication.java
    }
}


