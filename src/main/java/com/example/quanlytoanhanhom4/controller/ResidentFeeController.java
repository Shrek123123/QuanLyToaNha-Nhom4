package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.util.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ResidentFeeController implements Initializable {

    @FXML
    private Label balanceLabel;

    @FXML
    private VBox invoiceContainer;

    private int currentUserId;
    @FXML
    private Button dummyButton; // để load scene, giúp lookup Label

    private static final ScheduledExecutorService BADGE_UPDATER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ResidentFeeBadgeUpdater");
        t.setDaemon(true);
        return t;
    });

    static {
        // immediate refresh on class load (so when admin_main.fxml forces class to load,
        // badge will be updated right away), then schedule periodic refresh every 10s
        try {
            int initial = fetchPendingRequestCount();
            Platform.runLater(() -> updateBadgeInAllWindows(initial));
        } catch (Throwable ignored) {}

        BADGE_UPDATER.scheduleAtFixedRate(() -> {
            try {
                int cnt = fetchPendingRequestCount();
                Platform.runLater(() -> updateBadgeInAllWindows(cnt));
            } catch (Throwable ignored) {
            }
        }, 10, 10, TimeUnit.SECONDS); // first run after 10s, periodic every 10s
    }
    @FXML
    public void initialize() {
        // Update ngay khi load
        Platform.runLater(() -> {
            int cnt = fetchPendingRequestCount();
            updateBadgeInAllWindows(cnt);
        });
    }

    /**
     * Update số hóa đơn chưa thanh toán lên badge
     */


    /**
     * Lấy số hóa đơn chưa thanh toán từ database
     */
    private int fetchPendingPaymentCount() {
        String sql = "SELECT COUNT(*) AS cnt FROM fees_and_payments " +
                "WHERE user_id = ? AND (payment_status='not_payed' OR pass_deadline=1)";
        int count = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, UserSession.getCurrentUserId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("cnt");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Xử lý khi click vào nút "Hóa đơn & Thanh toán"
     */
    @FXML
    private void handleInvoice() {
        // TODO: mở cửa sổ quản lý hóa đơn
        System.out.println("Mở giao diện quản lý hóa đơn...");
    }

    // Tính số hóa đơn chưa thanh toán hoặc quá hạn
    public static int fetchPendingRequestCount() {
        String sql = "SELECT COUNT(*) AS cnt FROM fees_and_payments " +
                "WHERE assigned_to_resident_id = ? " +
                "AND (payment_status = 'not_payed' OR payment_status = 'past_deadline')";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return 0;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, UserSession.getCurrentUserId()); // dùng ID cư dân hiện tại
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("cnt");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null && !conn.isClosed()) conn.close(); } catch (Exception ignored) {}
        }
        return 0;
    }

    public static int fetchPendingCount() {
        int currentUserId = UserSession.getCurrentUserId();
        String sql = "SELECT COUNT(*) AS cnt FROM fees_and_payments " +
                "WHERE assigned_to_resident_id = ? " +
                "AND (payment_status = 'not_payed' OR payment_status = 'past_deadline')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Update badge Label trong tất cả các cửa sổ mở
    private static void updateBadgeInAllWindows(int cnt) {
        for (Window w : Window.getWindows()) {
            try {
                if (w.getScene() == null) continue;
                Node n = w.getScene().lookup("#residentFeePendingLabel");
                if (n instanceof Label) {
                    Label badge = (Label) n;
                    if (cnt > 0) {
                        badge.setText(String.valueOf(cnt));
                        badge.setVisible(true);
                    } else {
                        badge.setVisible(false);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    // Phương thức gọi khi mở giao diện chính hoặc nút
    public static void refreshBadgeNow() {
        BADGE_UPDATER.execute(() -> {
            int cnt = fetchPendingCount();
            Platform.runLater(() -> updateBadgeInAllWindows(cnt));
        });
    }

    static {
        // refresh định kỳ mỗi 10s
        BADGE_UPDATER.scheduleAtFixedRate(() -> {
            int cnt = fetchPendingCount();
            Platform.runLater(() -> updateBadgeInAllWindows(cnt));
        }, 0, 10, TimeUnit.SECONDS);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        currentUserId = UserSession.getCurrentUserId();
        loadBalance();
        loadInvoices();
    }

    private void loadBalance() {
        String sql = "SELECT balance FROM resident WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                balanceLabel.setText(String.format("Số dư: %.2f VNĐ", balance));
            } else {
                balanceLabel.setText("Số dư: 0.00 VNĐ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            balanceLabel.setText("Error");
        }
    }

    private void loadInvoices() {
        invoiceContainer.getChildren().clear();
        String sql = "SELECT * FROM fees_and_payments WHERE assigned_to_resident_id = ? ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            int count = 1;
            while (rs.next()) {
                VBox invoiceBox = new VBox(5);
                invoiceBox.setStyle("-fx-padding: 10; -fx-background-color: #2874A6; -fx-background-radius: 8;");

                Label title = new Label("Hóa đơn #" + count);
                title.setStyle("-fx-font-size: 16px; -fx-text-fill: white; -fx-font-weight: bold;");

                Label customer = new Label("Khách hàng: " + rs.getString("resident_name"));
                customer.setStyle("-fx-text-fill: white;");

                StringBuilder feeTypes = new StringBuilder();
                if (rs.getBoolean("electric_type")) feeTypes.append("Điện ");
                if (rs.getBoolean("water_type")) feeTypes.append("Nước ");
                if (rs.getBoolean("parking_type")) feeTypes.append("Gửi xe ");
                if (rs.getBoolean("services_type")) feeTypes.append("Dịch vụ ");
                if (rs.getBoolean("rent_type")) feeTypes.append("Thuê nhà ");

                Label feeTypeLabel = new Label("Loại phí: " + feeTypes.toString().trim());
                feeTypeLabel.setStyle("-fx-text-fill: white;");

                Label amountLabel = new Label("Số tiền phải đóng: " + rs.getDouble("payment_amount") + " VNĐ");
                amountLabel.setStyle("-fx-text-fill: white;");

                Label deadlineLabel = new Label("Hạn nộp: " + rs.getDate("deadline"));
                deadlineLabel.setStyle("-fx-text-fill: white;");

                Label creationLabel = new Label("Ngày tạo: " + rs.getDate("payment_creation_date"));
                creationLabel.setStyle("-fx-text-fill: white;");

                Button payButton = new Button("Thanh toán");
                payButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
                String paymentStatus = rs.getString("payment_status");

                if ("payed".equals(paymentStatus)) {
                    payButton.setDisable(true);
                    payButton.setText("Đã thanh toán");
                } else if ("past_deadline".equals(paymentStatus)) {
                    payButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white;");
                }

                int invoiceId = rs.getInt("id");
                double paymentAmount = rs.getDouble("payment_amount");
                payButton.setOnAction(event -> handlePayment(invoiceId, paymentAmount, payButton));

                invoiceBox.getChildren().addAll(title, customer, feeTypeLabel, amountLabel, deadlineLabel, creationLabel, payButton);
                invoiceContainer.getChildren().add(invoiceBox);
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handlePayment(int invoiceId, double amount, Button payButton) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Lấy số dư hiện tại
            double balance;
            try (PreparedStatement stmt = conn.prepareStatement("SELECT balance FROM resident WHERE user_id = ?")) {
                stmt.setInt(1, currentUserId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) return;
                balance = rs.getDouble("balance");
            }

            if (balance < amount) {
                // Hiển thị thông báo thất bại
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Thanh toán thất bại");
                alert.setHeaderText(null);
                alert.setContentText("Số dư không đủ để thanh toán.");
                alert.showAndWait();
                return;
            }

            // Cập nhật payment_status
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE fees_and_payments SET payment_status = 'payed', payment_date = CURRENT_DATE WHERE id = ?")) {
                stmt.setInt(1, invoiceId);
                stmt.executeUpdate();
            }

            // Trừ số dư
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE resident SET balance = balance - ? WHERE user_id = ?")) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, currentUserId);
                stmt.executeUpdate();
            }

            conn.commit();

            // Hiển thị thông báo thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thanh toán thành công");
            alert.setHeaderText(null);
            alert.setContentText("Hóa đơn đã được thanh toán thành công.");
            alert.showAndWait();

            payButton.setDisable(true);
            payButton.setText("Đã thanh toán");
            loadBalance();
            ResidentFeeController.refreshBadgeNow(); // Cập nhật badge sau thanh toán

        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText(null);
            alert.setContentText("Đã xảy ra lỗi khi thanh toán. Vui lòng thử lại.");
            alert.showAndWait();
        }
    }

}
