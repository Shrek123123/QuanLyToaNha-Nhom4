package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.ui.BuildingLogo;
import com.example.quanlytoanhanhom4.ui.DashboardView;
import com.example.quanlytoanhanhom4.util.AlertUtils;
import com.example.quanlytoanhanhom4.util.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.sql.*;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private Label residentGreeting;

    @FXML
    private Label residentStatus;

    @FXML
    private VBox residentList;

    @FXML
    private VBox contentPane;

    @FXML
    private ImageView flagImage;

    @FXML
    private HBox topBar;

    @FXML
    private Label adminLabel;

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

    @FXML
    private Button hrBtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Lấy thông tin từ UserSession
        String username = UserSession.getCurrentUsername();
        String role = UserSession.getCurrentRole();

        // Cập nhật label chào mừng
        if (adminLabel != null) {
            adminLabel.setText("CHÀO " + (username != null ? username.toUpperCase() : "ADMIN"));
        }

        // Thêm logo vào đầu topBar - dùng màu cho nền xanh
        if (topBar != null) {
            BuildingLogo logo = new BuildingLogo(60, 60, true); // true = cho nền xanh
            StackPane logoContainer = new StackPane(logo);
            logoContainer.setPadding(new Insets(0, 10, 0, 0));
            logoContainer.setMaxWidth(70); // Giới hạn kích thước để không vỡ giao diện
            logoContainer.setMaxHeight(70);
            topBar.getChildren().add(0, logoContainer);
        }
        Platform.runLater(this::loadResidentInfo);
        loadFlag();

    }

    private void openDashboard() {
        try {
            Stage currentStage = (Stage) topBar.getScene().getWindow();
            String role = UserSession.getCurrentRole();

            // Đảm bảo cửa sổ được maximize và resize
            currentStage.setResizable(true);

            DashboardView.show(currentStage, role != null ? role : "user");
            logger.debug("Đã mở dashboard cho role: {}", role);
        } catch (Exception e) {
            logger.error("Lỗi khi mở dashboard", e);
            AlertUtils.showError("Lỗi", "Không thể mở dashboard: " + e.getMessage());
        }
    }

    private void openModule(String fxmlPath, String title) {
        try {
            java.net.URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                logger.error("Không tìm thấy file FXML: {}", fxmlPath);
                AlertUtils.showError(
                        "Lỗi",
                        "Không tìm thấy file: " + fxmlPath + "\nVui lòng kiểm tra lại đường dẫn."
                );
                return;
            }

            logger.debug("Đang mở module: {} từ {}", title, fxmlPath);
            FXMLLoader loader = new FXMLLoader(resource);
            Stage moduleStage = new Stage();
            Scene scene = new Scene(loader.load());

            // Nếu là BMS controller, set tiêu đề cho label
            Object controller = loader.getController();
            if (controller instanceof BMSController) {
                ((BMSController) controller).setTitle(title);
            }

            moduleStage.setTitle(title);
            moduleStage.setScene(scene);

            // Cho phép resize và maximize
            moduleStage.setResizable(true);

            // Maximize cửa sổ để hiển thị toàn màn hình
            moduleStage.setMaximized(true);

            moduleStage.show();
            logger.info("Đã mở cửa sổ: {}", title);
        } catch (Exception e) {
            logger.error("Lỗi khi mở cửa sổ: {}", title, e);
            AlertUtils.showError(
                    "Lỗi",
                    "Không thể mở cửa sổ: " + title + "\n" + e.getMessage()
            );
        }
    }

    @FXML
    private void handleDashboard() {
        openDashboard();
    }

    @FXML
    private void handleBms() {
        openModule("/com/example/quanlytoanhanhom4/fxml/bms.fxml", "Giám sát hệ thống");
    }

    @FXML
    private void handlePccc() {
        openModule("/com/example/quanlytoanhanhom4/fxml/bms.fxml", "PCCC & Khẩn cấp");
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
    private void handlePayment() {
        openModule("/com/example/quanlytoanhanhom4/fxml/fee_and_payment.fxml", "Quản lý thanh toán dân cư");
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
    private void handleHR() {
        openModule("/com/example/quanlytoanhanhom4/fxml/hr.fxml", "Quản lý Nhân sự & Chấm công");
    }

    @FXML
    private void handleResident() {
        openModule("/com/example/quanlytoanhanhom4/fxml/resident.fxml", "Quản lý Cư dân");
    }

    @FXML
    private void handleResidentAdmin() {
        openModule("/com/example/quanlytoanhanhom4/fxml/admin_resident_control.fxml", "Quản lý Cư dân (Admin)");
    }

    @FXML
    private void handleResidentCard() {
        openModule("/com/example/quanlytoanhanhom4/fxml/resident_card.fxml", "Trạng thái thẻ dân cư");
    }
    @FXML
    private void handleAdminResidentCard() {
        openModule("/com/example/quanlytoanhanhom4/fxml/admin_resident_card.fxml", "Trạng thái thẻ dân cư");
    }

    @FXML
    private void handleUtility() {
        openModule("/com/example/quanlytoanhanhom4/fxml/utility.fxml", "Quản lý Điện - Nước - Phí dịch vụ");
    }
    @FXML
    private void handleResidentFee() {
        openModule("/com/example/quanlytoanhanhom4/fxml/resident_fee_and_payment.fxml", "Hóa đơn cần thanh toán");
    }

    @FXML
    private void handleInvoice() {
        openModule("/com/example/quanlytoanhanhom4/fxml/invoice.fxml", "Hóa đơn & Thanh toán");
    }

    @FXML
    private void handleNotification() {
        openModule("/com/example/quanlytoanhanhom4/fxml/notification.fxml", "Gửi thông báo");
    }

    @FXML
    private void handleRepairRequest() {
        openModule("/com/example/quanlytoanhanhom4/fxml/repair_request.fxml", "Quản lý Yêu cầu Sửa chữa");
    }
    @FXML
    private void handleAddMoney() {
        openModule("/com/example/quanlytoanhanhom4/fxml/resident_add_money.fxml", "Nạp tiền cho dân cư");
    }

    @FXML
    private void handleLogout() {
        try {
            Stage currentStage = (Stage) topBar.getScene().getWindow();
            currentStage.close();

            // Xóa thông tin session
            UserSession.clear();
            logger.info("User đã đăng xuất");

            // Mở lại màn hình đăng nhập
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/quanlytoanhanhom4/fxml/login.fxml"));
            Stage loginStage = new Stage();
            Scene scene = new Scene(loader.load(), 400, 350);
            loginStage.setTitle("Đăng nhập quản lý toà nhà");
            loginStage.setResizable(false);
            loginStage.setScene(scene);
            loginStage.show();
            logger.debug("Đã mở lại màn hình đăng nhập");
        } catch (Exception e) {
            logger.error("Lỗi khi đăng xuất", e);
            AlertUtils.showError("Lỗi", "Không thể đăng xuất: " + e.getMessage());
        }
    }
    private void loadResidentInfo() {
        try {
            int userId = UserSession.getCurrentUserId(); // Lấy từ session
            var con = DatabaseConnection.getConnection();

            // Lấy resident_id và full_name + is_owner
            String sql = "SELECT r.full_name, r.is_owner, r.resident_id AS resident_id " +
                    "FROM resident r JOIN user u ON u.id = r.resident_id " +
                    "WHERE u.id = ?";
            var ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            var rs = ps.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("full_name");
                boolean isOwner = rs.getInt("is_owner") == 1;
                int residentId = rs.getInt("resident_id");

                residentGreeting.setText("Chào cư dân " + fullName + "!");
                residentStatus.setText(isOwner ?
                        "Bạn là chủ căn hộ, chuẩn bị đóng tiền đi nha 😏" :
                        "Bạn tuy không phải chủ căn hộ, nhưng bạn vẫn có đủ dịch vụ như chủ vậy!");

                // Lấy danh sách thành viên căn hộ từ resident_card
                String apartmentSql = "SELECT apartment_id FROM resident_card WHERE resident_id = ?";
                var ps2 = con.prepareStatement(apartmentSql);
                ps2.setInt(1, residentId);
                var rs2 = ps2.executeQuery();

                int apartmentId = -1;
                if(rs2.next()) apartmentId = rs2.getInt("apartment_id");

                if(apartmentId != -1){
                    String membersSql = "SELECT resident_name, card_type FROM resident_card WHERE apartment_id = ?";
                    var ps3 = con.prepareStatement(membersSql);
                    ps3.setInt(1, apartmentId);
                    var rs3 = ps3.executeQuery();

                    residentList.getChildren().clear();
                    int count = 1;
                    while(rs3.next()){
                        String name = rs3.getString("resident_name");
                        String type = rs3.getString("card_type");
                        Label lbl = new Label(count + ". " + name + " (" + type + ")");
                        lbl.getStyleClass().add("resident-item");
                        residentList.getChildren().add(lbl);
                        count++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFlag() {
        try {
            InputStream is = getClass().getResourceAsStream("/images/vn-flag.jpg");
            if(is != null) {
                flagImage.setImage(new Image(is));
            } else {
                System.err.println("Không tìm thấy file vn.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}