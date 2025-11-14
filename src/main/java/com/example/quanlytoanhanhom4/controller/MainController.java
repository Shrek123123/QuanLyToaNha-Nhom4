package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.ui.DashboardView;
import com.example.quanlytoanhanhom4.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    @FXML
    private HBox topBar;
    
    @FXML
    private Label adminLabel;
    
    @FXML
    private Label roleLabel;
    
    @FXML
    private Button dashboardBtn;
    
    @FXML
    private Button dienBtn;
    
    @FXML
    private Button pcccBtn;
    
    @FXML
    private Button chieuSangBtn;
    
    @FXML
    private Button baoTriBtn;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Lấy thông tin từ UserSession
        String username = UserSession.getCurrentUsername();
        String role = UserSession.getCurrentRole();
        
        // Cập nhật label chào mừng
        if (adminLabel != null) {
            adminLabel.setText("CHÀO " + (username != null ? username.toUpperCase() : "ADMIN"));
        }
        
        if (roleLabel != null) {
            roleLabel.setText(role != null ? role.toUpperCase() : "ADMIN");
        }
        
    }
    
    private void openDashboard() {
        try {
            Stage currentStage = (Stage) topBar.getScene().getWindow();
            String role = UserSession.getCurrentRole();
            DashboardView.show(currentStage, role != null ? role : "user");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void openModule(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage moduleStage = new Stage();
            Scene scene = new Scene(loader.load(), 1000, 600);
            moduleStage.setTitle(title);
            moduleStage.setScene(scene);
            moduleStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDashboard() {
        openDashboard();
    }
    
    @FXML
    private void handleBms() {
        openModule("/com/example/quanlytoanhanhom4/fxml/bms.fxml", "Giám sát & Điều khiển (BMS)");
    }
    
    @FXML
    private void handlePccc() {
        openModule("/com/example/quanlytoanhanhom4/fxml/bms.fxml", "Phòng cháy chữa cháy");
    }
    
    @FXML
    private void handleLighting() {
        openModule("/com/example/quanlytoanhanhom4/fxml/bms.fxml", "Chiếu sáng & Tiện ích");
    }
    
    @FXML
    private void handleMaintenance() {
        openModule("/com/example/quanlytoanhanhom4/fxml/maintenance.fxml", "Quản lý Bảo trì");
    }
    
    @FXML
    private void handleSecurity() {
        openModule("/com/example/quanlytoanhanhom4/fxml/security.fxml", "Quản lý An ninh");
    }
    
    @FXML
    private void handleCleaning() {
        openModule("/com/example/quanlytoanhanhom4/fxml/cleaning.fxml", "Quản lý Vệ sinh");
    }
    
    @FXML
    private void handleCustomer() {
        openModule("/com/example/quanlytoanhanhom4/fxml/customer.fxml", "Quản lý Khách hàng");
    }
    
    @FXML
    private void handleAdmin() {
        openModule("/com/example/quanlytoanhanhom4/fxml/admin.fxml", "Quản lý Hành chính & Nhân sự");
    }
    
    @FXML
    private void handleLogout() {
        try {
            Stage currentStage = (Stage) topBar.getScene().getWindow();
            currentStage.close();
            
            // Xóa thông tin session
            UserSession.clear();
            
            // Mở lại màn hình đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/quanlytoanhanhom4/fxml/login.fxml"));
            Stage loginStage = new Stage();
            Scene scene = new Scene(loader.load(), 400, 350);
            loginStage.setTitle("Đăng nhập quản lý toà nhà");
            loginStage.setResizable(false);
            loginStage.setScene(scene);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

