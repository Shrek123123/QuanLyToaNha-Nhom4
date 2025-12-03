package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AdminResidentCardController implements Initializable {

    @FXML private TableView<ResidentCardRow> cardsTable;
    @FXML private TableColumn<ResidentCardRow, Integer> colId;
    @FXML private TableColumn<ResidentCardRow, Integer> colResidentId;
    @FXML private TableColumn<ResidentCardRow, Integer> colApartmentId;
    @FXML private TableColumn<ResidentCardRow, String> colApartmentNo;
    @FXML private TableColumn<ResidentCardRow, String> colCardNumber;
    @FXML private TableColumn<ResidentCardRow, String> colResidentName;
    @FXML private TableColumn<ResidentCardRow, String> colCardType;
    @FXML private TableColumn<ResidentCardRow, Date> colDateIssued;
    @FXML private TableColumn<ResidentCardRow, String> colRequest;

    @FXML private Button requestButton;
    @FXML private Label statusLabel;

    // form controls
    @FXML private ComboBox<UserItem> residentCombo;
    @FXML private TextField cardNumberField;
    @FXML private ChoiceBox<String> cardTypeChoice;
    @FXML private TextField apartmentIdField;
    @FXML private Label dateIssuedLabel;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final ObservableList<ResidentCardRow> cardList = FXCollections.observableArrayList();
    private final ObservableList<UserItem> residentList = FXCollections.observableArrayList();

    // selected row id for update/delete (-1 = none)
    private int selectedRowId = -1;

    // background updater (single-thread) to refresh badge periodically
    private static final ScheduledExecutorService BADGE_UPDATER = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "ResidentCardBadgeUpdater");
        t.setDaemon(true);
        return t;
    });

    static {
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
        }, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // table bindings
        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colResidentId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("residentId"));
        colApartmentId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("apartmentId"));
        colApartmentNo.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("apartmentNo"));
        colCardNumber.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cardNumber"));
        colResidentName.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("residentName"));
        colCardType.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cardType"));
        colDateIssued.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dateIssued"));
        colRequest.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("requestForRenewalText"));

        cardsTable.setItems(cardList);

        // disable request button when no selection
        requestButton.disableProperty().bind(cardsTable.getSelectionModel().selectedItemProperty().isNull());

        // setup card type choices
        cardTypeChoice.setItems(FXCollections.observableArrayList("resident", "guest"));
        cardTypeChoice.setValue("resident");

        // load initial data
        loadResidentsFromDB();
        loadAllCards();

        // selection listener: fill form when row selected
        cardsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateFormFromRow(newSel);
            } else {
                clearFormFields();
            }
        });

        // ensure buttons state
        updateButton.setDisable(true);
        deleteButton.setDisable(true);

        // when resident selected update displayed resident_name automatically
        residentCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                // nothing else needed; resident_name will be saved from selection when inserting/updating
            }
        });

        // initial badge refresh
        Platform.runLater(() -> {
            int cnt = fetchPendingRequestCount();
            updateBadgeInAllWindows(cnt);
        });
    }

    // ------------------ UI handlers ------------------

    @FXML
    private void handleRefresh() {
        loadResidentsFromDB();
        loadAllCards();
        statusLabel.setText("Đã làm mới dữ liệu.");
    }

    @FXML
    private void handleRequestNewCard() {
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
                statusLabel.setText("Đã duyệt cấp thẻ cho id=" + selected.getId());
                loadAllCards();
            } else {
                statusLabel.setText("Cập nhật thất bại cho id=" + selected.getId());
            }
        } catch (Exception e) {
            statusLabel.setText("Lỗi khi duyệt yêu cầu.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddCard() {
        UserItem user = residentCombo.getValue();
        String cardNum = cardNumberField.getText().trim();
        String cardType = cardTypeChoice.getValue();
        String aptText = apartmentIdField.getText().trim();

        if (user == null || cardNum.isEmpty() || cardType == null || aptText.isEmpty()) {
            statusLabel.setText("Vui lòng điền đầy đủ dữ liệu (resident, card number, card type, apartment id).");
            return;
        }

        int apartmentId;
        try {
            apartmentId = Integer.parseInt(aptText);
        } catch (NumberFormatException ex) {
            statusLabel.setText("Apartment ID phải là số nguyên.");
            return;
        }

        String insertSql = "INSERT INTO resident_card (resident_id, apartment_id, card_number, resident_name, card_type, date_issued, request_for_renewal) VALUES (?, ?, ?, ?, ?, ?, 0)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, apartmentId);
            ps.setString(3, cardNum);
            ps.setString(4, user.getUsername());
            ps.setString(5, cardType);
            ps.setDate(6, Date.valueOf(LocalDate.now()));

            int inserted = ps.executeUpdate();
            if (inserted > 0) {
                statusLabel.setText("Thêm thẻ thành công.");
                clearFormFields();
                loadAllCards();
                loadResidentsFromDB(); // refresh resident listing (if you depend on resident_card rows)
            } else {
                statusLabel.setText("Thêm thẻ thất bại.");
            }

        } catch (Exception e) {
            statusLabel.setText("Lỗi khi thêm thẻ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateCard() {
        if (selectedRowId == -1) {
            statusLabel.setText("Vui lòng chọn một hàng để sửa.");
            return;
        }

        UserItem user = residentCombo.getValue();
        String cardNum = cardNumberField.getText().trim();
        String cardType = cardTypeChoice.getValue();
        String aptText = apartmentIdField.getText().trim();

        if (user == null || cardNum.isEmpty() || cardType == null || aptText.isEmpty()) {
            statusLabel.setText("Vui lòng điền đầy đủ dữ liệu (resident, card number, card type, apartment id).");
            return;
        }

        int apartmentId;
        try {
            apartmentId = Integer.parseInt(aptText);
        } catch (NumberFormatException ex) {
            statusLabel.setText("Apartment ID phải là số nguyên.");
            return;
        }

        String updateSql = "UPDATE resident_card SET resident_id = ?, apartment_id = ?, card_number = ?, resident_name = ?, card_type = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, apartmentId);
            ps.setString(3, cardNum);
            ps.setString(4, user.getUsername());
            ps.setString(5, cardType);
            ps.setInt(6, selectedRowId);

            int changed = ps.executeUpdate();
            if (changed > 0) {
                statusLabel.setText("Cập nhật thẻ id=" + selectedRowId + " thành công.");
                loadAllCards();
            } else {
                statusLabel.setText("Cập nhật thất bại cho id=" + selectedRowId);
            }

        } catch (Exception e) {
            statusLabel.setText("Lỗi khi cập nhật thẻ.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteCard() {
        if (selectedRowId == -1) {
            statusLabel.setText("Vui lòng chọn một hàng để xóa.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa");
        confirm.setHeaderText(null);
        confirm.setContentText("Bạn có chắc muốn xóa thẻ id=" + selectedRowId + " không?");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        String deleteSql = "DELETE FROM resident_card WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {

            ps.setInt(1, selectedRowId);
            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                statusLabel.setText("Xóa thành công id=" + selectedRowId);
                clearFormFields();
                loadAllCards();
                loadResidentsFromDB();
            } else {
                statusLabel.setText("Xóa thất bại cho id=" + selectedRowId);
            }

        } catch (Exception e) {
            statusLabel.setText("Lỗi khi xóa thẻ.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearForm() {
        cardsTable.getSelectionModel().clearSelection();
        clearFormFields();
    }

    // ------------------ DB load methods ------------------

    /**
     * Load cards and join apartment to get apartment_no
     */
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
                        rs.getString("apartment_no"),
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

    /**
     * Load resident list for ComboBox: requirement said "tìm các value trong cột resident_id trong bảng resident_card và link sang id bên bảng user để lấy ra cột username"
     * => SELECT DISTINCT u.id, u.username FROM user u JOIN resident_card rc ON rc.resident_id = u.id
     *
     * If you prefer to load all users, change the query accordingly.
     */
    private void loadResidentsFromDB() {
        residentList.clear();
        String sql = "SELECT DISTINCT u.id, u.username FROM user u JOIN resident_card rc ON rc.resident_id = u.id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                residentList.add(new UserItem(rs.getInt("id"), rs.getString("username")));
            }
        } catch (Exception e) {
            // fallback: if join fails or returns empty, attempt to load all users
            try {
                residentList.clear();
                String sql2 = "SELECT id, username FROM user";
                try (Connection conn2 = DatabaseConnection.getConnection();
                     PreparedStatement ps2 = conn2.prepareStatement(sql2);
                     ResultSet rs2 = ps2.executeQuery()) {
                    while (rs2.next()) {
                        residentList.add(new UserItem(rs2.getInt("id"), rs2.getString("username")));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            residentCombo.setItems(residentList);
            residentCombo.setEditable(false);
        }
    }

    // ------------------ Helpers ------------------

    private void populateFormFromRow(ResidentCardRow row) {
        if (row == null) return;
        selectedRowId = row.getId();
        // select resident in combo (match by id)
        for (UserItem u : residentList) {
            if (u.getId() == row.getResidentId()) {
                residentCombo.setValue(u);
                break;
            }
        }
        cardNumberField.setText(row.getCardNumber());
        cardTypeChoice.setValue(row.getCardType());
        apartmentIdField.setText(String.valueOf(row.getApartmentId()));
        dateIssuedLabel.setText(row.getDateIssued() != null ? row.getDateIssued().toString() : "(null)");

        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void clearFormFields() {
        selectedRowId = -1;
        residentCombo.setValue(null);
        cardNumberField.clear();
        cardTypeChoice.setValue("resident");
        apartmentIdField.clear();
        dateIssuedLabel.setText("(auto)");
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    // ------------ badge helpers (unchanged) --------------

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
            e.printStackTrace();
        } finally {
            try { if (conn != null && !conn.isClosed()) conn.close(); } catch (Exception ignored) {}
        }
        return 0;
    }

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

    private static void updateBadgeInAllWindows(int cnt) {
        try {
            for (javafx.stage.Window w : javafx.stage.Window.getWindows()) {
                if (w.getScene() == null) continue;
                javafx.scene.Node n = w.getScene().lookup("#residentCardPendingLabel");
                if (n instanceof Label) {
                    Label badge = (Label) n;
                    if (cnt > 0) {
                        badge.setText(String.valueOf(cnt));
                        badge.setVisible(true);
                    } else {
                        badge.setVisible(false);
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    public static void refreshBadgeNow() {
        BADGE_UPDATER.execute(() -> {
            int cnt = fetchPendingRequestCount();
            Platform.runLater(() -> updateBadgeInAllWindows(cnt));
        });
    }

    // ------------------ Model classes ------------------

    public static class ResidentCardRow {
        private final int id;
        private final int residentId;
        private final int apartmentId;
        private final String apartmentNo;
        private final String cardNumber;
        private final String residentName;
        private final String cardType;
        private final Date dateIssued;
        private final boolean requestForRenewal;

        public ResidentCardRow(int id, int residentId, int apartmentId, String apartmentNo,
                               String cardNumber, String residentName, String cardType, Date dateIssued, boolean requestForRenewal) {
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
        public String getRequestForRenewalText() { return requestForRenewal ? "YES" : "NO"; }
    }

    /**
     * Simple holder for ComboBox entries
     */
    public static class UserItem {
        private final int id;
        private final String username;
        public UserItem(int id, String username) { this.id = id; this.username = username; }
        public int getId() { return id; }
        public String getUsername() { return username; }
        @Override
        public String toString() { return username; } // shown in ComboBox
    }
}
