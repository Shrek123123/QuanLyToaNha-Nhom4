package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import com.example.quanlytoanhanhom4.util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.application.Platform; // added import

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ResourceBundle;

import javafx.stage.Window;
import javafx.scene.Node;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AdminResidentCardController implements Initializable {

    @FXML private TableView<ResidentCardRow> cardsTable;
    @FXML private TableColumn<ResidentCardRow, Integer> colId;
    @FXML private TableColumn<ResidentCardRow, Integer> colResidentId;
    @FXML private TableColumn<ResidentCardRow, Integer> colApartmentId;
    @FXML private TableColumn<ResidentCardRow, String> colApartmentNo; // new column
    @FXML private TableColumn<ResidentCardRow, String> colCardNumber;
    @FXML private TableColumn<ResidentCardRow, String> colResidentName;
    @FXML private TableColumn<ResidentCardRow, String> colCardType;
    @FXML private TableColumn<ResidentCardRow, Date> colDateIssued;
    @FXML private TableColumn<ResidentCardRow, String> colRequest;

    @FXML private Button requestButton;
    @FXML private Label statusLabel;

    private final ObservableList<ResidentCardRow> cardList = FXCollections.observableArrayList();

    // background updater (single-thread) to refresh badge periodically
    private static final ScheduledExecutorService BADGE_UPDATER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ResidentCardBadgeUpdater");
        t.setDaemon(true);
        return t;
    });

    static {
        // schedule periodic badge refresh (initial delay 1s, then every 10s)
        BADGE_UPDATER.scheduleAtFixedRate(() -> {
            try {
                int cnt = fetchPendingRequestCount();
                // update any matching label on FX thread
                Platform.runLater(() -> updateBadgeInAllWindows(cnt));
            } catch (Throwable ignored) {
            }
        }, 1, 10, TimeUnit.SECONDS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set cell value factories by property names
        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colResidentId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("residentId"));
        colApartmentId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("apartmentId"));
        colApartmentNo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("apartmentNo")); // bind apartment_no
        colCardNumber.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cardNumber"));
        colResidentName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("residentName"));
        colCardType.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cardType"));
        colDateIssued.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dateIssued"));
        colRequest.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("requestForRenewalText"));

        cardsTable.setItems(cardList);

        // disable button when no selection
        requestButton.disableProperty().bind(cardsTable.getSelectionModel().selectedItemProperty().isNull());

        // load all cards for admin view
        loadAllCards();

        // Also trigger an immediate badge refresh when this controller is initialized
        Platform.runLater(() -> {
            int cnt = fetchPendingRequestCount();
            updateBadgeInAllWindows(cnt);
        });
    }

    @FXML
    private void handleRefresh() {
        loadAllCards();
    }

    @FXML
    private void handleRequestNewCard() {
        // For admin view this button approves the pending request: set request_for_renewal = 0
        ResidentCardRow selected = cardsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Vui lòng chọn một thẻ.");
            return;
        }
        String updateSql = "UPDATE resident_card SET request_for_renewal = 0 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {

            ps.setInt(1, selected.getId());
            int changed = ps.executeUpdate();
            if (changed > 0) {
                selected.setRequestForRenewal(false);
                cardsTable.refresh();
                statusLabel.setText("Đã duyệt cấp thẻ cho id=" + selected.getId());
            } else {
                statusLabel.setText("Cập nhật thất bại cho id=" + selected.getId());
            }
        } catch (Exception e) {
            statusLabel.setText("Lỗi khi duyệt yêu cầu.");
            e.printStackTrace();
        }
    }

    // NEW: load all resident_card rows joined with apartment to include apartment_no
    private void loadAllCards() {
        cardList.clear();
        String sql = "SELECT rc.id, rc.resident_id, rc.apartment_id, a.apartment_no, rc.card_number, rc.resident_name, rc.card_type, rc.date_issued, rc.request_for_renewal " +
                     "FROM resident_card rc LEFT JOIN apartment a ON rc.apartment_id = a.id ORDER BY rc.id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            boolean any = false;
            while (rs.next()) {
                any = true;
                ResidentCardRow row = new ResidentCardRow(
                        rs.getInt("id"),
                        rs.getInt("resident_id"),
                        rs.getInt("apartment_id"),
                        rs.getString("apartment_no"), // from JOIN
                        rs.getString("card_number"),
                        rs.getString("resident_name"),
                        rs.getString("card_type"),
                        rs.getDate("date_issued"),
                        rs.getBoolean("request_for_renewal")
                );
                cardList.add(row);
            }
            if (!any) {
                statusLabel.setText("Không có thẻ trong hệ thống.");
            } else {
                statusLabel.setText("Đã nạp " + cardList.size() + " thẻ.");
            }
        } catch (Exception e) {
            statusLabel.setText("Lỗi khi nạp thẻ.");
            e.printStackTrace();
        }
    }

    // NEW: returns count of pending resident_card requests (request_for_renewal = 1)
    public static int fetchPendingRequestCount() {
        String sql = "SELECT COUNT(*) AS cnt FROM resident_card WHERE request_for_renewal = 1";
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) return 0;
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (Exception e) {
            // keep simple: log to console for now
            e.printStackTrace();
        } finally {
            try { if (conn != null && !conn.isClosed()) conn.close(); } catch (Exception ignored) {}
        }
        return 0;
    }

    // NEW: update a badge Label on the JavaFX thread based on pending count
    public void updatePendingBadge(Label badge) {
        if (badge == null) return;
        int cnt = fetchPendingRequestCount();
        Platform.runLater(() -> {
            if (cnt > 0) {
                badge.setText(String.valueOf(cnt));
                badge.setVisible(true);
            } else {
                badge.setVisible(false);
            }
        });
    }

    // NEW: update any Label with fx:id "residentCardPendingLabel" across all open windows
    private static void updateBadgeInAllWindows(int cnt) {
        for (Window w : Window.getWindows()) {
            try {
                if (w.getScene() == null) continue;
                Node n = w.getScene().lookup("#residentCardPendingLabel");
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

    // Public method to force immediate refresh from other controllers if desired
    public static void refreshBadgeNow() {
        BADGE_UPDATER.execute(() -> {
            int cnt = fetchPendingRequestCount();
            Platform.runLater(() -> updateBadgeInAllWindows(cnt));
        });
    }

    // simple model class for table rows
    public static class ResidentCardRow {
        private final int id;
        private final int residentId;
        private final int apartmentId;
        private final String apartmentNo; // new field
        private final String cardNumber;
        private final String residentName;
        private final String cardType;
        private final Date dateIssued;
        private boolean requestForRenewal;

        public ResidentCardRow(int id, int residentId, int apartmentId, String apartmentNo, String cardNumber, String residentName, String cardType, Date dateIssued, boolean requestForRenewal) {
            this.id = id;
            this.residentId = residentId;
            this.apartmentId = apartmentId;
            this.apartmentNo = apartmentNo;
            this.cardNumber = cardNumber;
            this.residentName = residentName;
            this.cardType = cardType;
            this.dateIssued = dateIssued;
            this.requestForRenewal = requestForRenewal;
        }

        public int getId() { return id; }
        public int getResidentId() { return residentId; }
        public int getApartmentId() { return apartmentId; }
        public String getApartmentNo() { return apartmentNo; }
        public String getCardNumber() { return cardNumber; }
        public String getResidentName() { return residentName; }
        public String getCardType() { return cardType; }
        public Date getDateIssued() { return dateIssued; }

        public boolean isRequestForRenewal() { return requestForRenewal; }
        public void setRequestForRenewal(boolean requestForRenewal) { this.requestForRenewal = requestForRenewal; }

        // text for display in request column
        public String getRequestForRenewalText() {
            return requestForRenewal ? "YES" : "NO";
        }
    }
}

