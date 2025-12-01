package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResidentAddMoneyController {

    @FXML
    private TextField amountField;

    @FXML
    private Button submitButton;

    @FXML
    private Label balanceLabel;

    private int currentUserId;

    @FXML
    public void initialize() {
        currentUserId = UserSession.getCurrentUserId();
        loadBalance();
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

    @FXML
    private void handleAddMoney() {
        String input = amountField.getText().trim();
        if (input.isEmpty()) {
            showAlert("Vui lòng nhập số tiền.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(input);
            if (amount <= 0) {
                showAlert("Số tiền phải lớn hơn 0.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Số tiền không hợp lệ.");
            return;
        }

        String sql = "UPDATE resident SET balance = balance + ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, currentUserId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                showAlert("Nạp tiền thành công: " + amount + " VNĐ");
                amountField.clear();
                loadBalance(); // cập nhật số dư mới
            } else {
                showAlert("Không tìm thấy tài khoản để nạp tiền.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi nạp tiền.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
