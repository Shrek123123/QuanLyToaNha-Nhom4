package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.config.DatabaseConnection;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FeeAndPaymentController {

    @FXML private TableView<FeeRecord> feeTable;
    @FXML private TableColumn<FeeRecord, Number> colId;
    @FXML private TableColumn<FeeRecord, Number> colResidentId;
    @FXML private TableColumn<FeeRecord, String> colResidentName;
    @FXML private TableColumn<FeeRecord, Boolean> colElectric;
    @FXML private TableColumn<FeeRecord, Boolean> colWater;
    @FXML private TableColumn<FeeRecord, Boolean> colParking;
    @FXML private TableColumn<FeeRecord, Boolean> colServices;
    @FXML private TableColumn<FeeRecord, Boolean> colRent;
    @FXML private TableColumn<FeeRecord, String> colAmount;
    @FXML private TableColumn<FeeRecord, LocalDate> colDeadline;
    @FXML private TableColumn<FeeRecord, String> colStatus;
    @FXML private TableColumn<FeeRecord, LocalDate> colPaymentDate;
    @FXML private TableColumn<FeeRecord, LocalDate> colCreationDate;

    @FXML private TextField residentIdField;
    @FXML private TextField residentNameField;
    @FXML private ComboBox<String> feeTypeCombo;
    @FXML private TextField amountField;
    @FXML private DatePicker deadlinePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private DatePicker paymentDatePicker;

    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;

    private final ObservableList<FeeRecord> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // setup combo boxes
        feeTypeCombo.getItems().addAll("Tiền gửi xe", "Tiền thuê nhà", "Phí dịch vụ", "Tiền điện", "Tiền nước");
        feeTypeCombo.getSelectionModel().selectFirst();

        statusCombo.getItems().addAll("not_payed", "payed", "past_deadline");
        statusCombo.getSelectionModel().select("not_payed");

        // setup table columns
        colId.setCellValueFactory(cell -> cell.getValue().idProperty());
        colResidentId.setCellValueFactory(cell -> cell.getValue().residentIdProperty());
        colResidentName.setCellValueFactory(cell -> cell.getValue().residentNameProperty());
        colElectric.setCellValueFactory(cell -> cell.getValue().electricProperty());
        colWater.setCellValueFactory(cell -> cell.getValue().waterProperty());
        colParking.setCellValueFactory(cell -> cell.getValue().parkingProperty());
        colServices.setCellValueFactory(cell -> cell.getValue().servicesProperty());
        colRent.setCellValueFactory(cell -> cell.getValue().rentProperty());
        colAmount.setCellValueFactory(cell -> cell.getValue().amountStringProperty());

        // date columns: bind to LocalDate properties
        colDeadline.setCellValueFactory(cell -> cell.getValue().deadlineProperty());
        colPaymentDate.setCellValueFactory(cell -> cell.getValue().paymentDateProperty());
        colCreationDate.setCellValueFactory(cell -> cell.getValue().creationDateProperty());

        // format LocalDate display as dd/MM/yyyy
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Callback<TableColumn<FeeRecord, LocalDate>, TableCell<FeeRecord, LocalDate>> dateCellFactory =
                col -> new TableCell<>() {
                    @Override
                    protected void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.format(df));
                        }
                    }
                };

        colDeadline.setCellFactory(dateCellFactory);
        colPaymentDate.setCellFactory(dateCellFactory);
        colCreationDate.setCellFactory(dateCellFactory);

        colStatus.setCellValueFactory(cell -> cell.getValue().statusProperty());

        feeTable.setItems(data);

        // row selection -> populate form
        feeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateForm(newSel);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                clearForm();
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        loadData();
    }

    private void populateForm(FeeRecord r) {
        residentIdField.setText(String.valueOf(r.getResidentId()));
        residentNameField.setText(r.getResidentName());
        // choose fee type by checking which boolean is true (prefer first true)
        if (r.isParking()) feeTypeCombo.getSelectionModel().select("Tiền gửi xe");
        else if (r.isRent()) feeTypeCombo.getSelectionModel().select("Tiền thuê nhà");
        else if (r.isServices()) feeTypeCombo.getSelectionModel().select("Phí dịch vụ");
        else if (r.isElectric()) feeTypeCombo.getSelectionModel().select("Tiền điện");
        else if (r.isWater()) feeTypeCombo.getSelectionModel().select("Tiền nước");
        else feeTypeCombo.getSelectionModel().selectFirst();

        amountField.setText(r.getAmount().toPlainString());
        deadlinePicker.setValue(r.getDeadline());
        statusCombo.getSelectionModel().select(r.getStatus());
        paymentDatePicker.setValue(r.getPaymentDate());
    }

    private void clearForm() {
        residentIdField.clear();
        residentNameField.clear();
        feeTypeCombo.getSelectionModel().selectFirst();
        amountField.clear();
        deadlinePicker.setValue(null);
        statusCombo.getSelectionModel().select("not_payed");
        paymentDatePicker.setValue(null);
        feeTable.getSelectionModel().clearSelection();
    }

    private Connection getConnection() throws SQLException {
        Connection c = DatabaseConnection.getConnection();
        if (c == null) throw new SQLException("Database not configured (DatabaseConnection.getConnection returned null).");
        return c;
    }

    private void loadData() {
        data.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                setStatus("DB not configured. Table not loaded.");
                return;
            }
            String sql = "SELECT * FROM fees_and_payments ORDER BY id DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FeeRecord r = new FeeRecord(
                            rs.getInt("id"),
                            rs.getInt("assigned_to_resident_id"),
                            rs.getString("resident_name"),
                            rs.getBoolean("electric_type"),
                            rs.getBoolean("water_type"),
                            rs.getBoolean("parking_type"),
                            rs.getBoolean("services_type"),
                            rs.getBoolean("rent_type"),
                            rs.getBigDecimal("payment_amount"),
                            toLocalDate(rs.getDate("deadline")),
                            rs.getString("payment_status"),
                            toLocalDate(rs.getDate("payment_date")),
                            toLocalDate(rs.getDate("payment_creation_date"))
                    );
                    data.add(r);
                }
                setStatus("Loaded " + data.size() + " record(s).");
            }
        } catch (SQLException ex) {
            setStatus("Error loading data: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static LocalDate toLocalDate(Date d) {
        return d == null ? null : d.toLocalDate();
    }

    private void setStatus(String msg) {
        Platform.runLater(() -> statusLabel.setText(msg));
    }

    @FXML
    private void handleAdd() {
        String residentIdText = residentIdField.getText().trim();
        String residentName = residentNameField.getText().trim();
        String feeType = feeTypeCombo.getSelectionModel().getSelectedItem();
        String amountText = amountField.getText().trim();
        LocalDate deadline = deadlinePicker.getValue();

        if (residentIdText.isEmpty() || residentName.isEmpty() || amountText.isEmpty() || deadline == null) {
            setStatus("Vui lòng điền đầy đủ: ID cư dân, tên, số tiền, hạn thanh toán.");
            return;
        }
        int residentId;
        BigDecimal amount;
        try {
            residentId = Integer.parseInt(residentIdText);
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            setStatus("ID cư dân hoặc số tiền không hợp lệ.");
            return;
        }

        boolean electric=false, water=false, parking=false, services=false, rent=false;
        switch (feeType) {
            case "Tiền gửi xe": parking = true; break;
            case "Tiền thuê nhà": rent = true; break;
            case "Phí dịch vụ": services = true; break;
            case "Tiền điện": electric = true; break;
            case "Tiền nước": water = true; break;
        }

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO fees_and_payments (assigned_to_resident_id, resident_name, electric_type, water_type, parking_type, services_type, rent_type, payment_amount, deadline, payment_status, payment_date, payment_creation_date) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, residentId);
                ps.setString(2, residentName);
                ps.setBoolean(3, electric);
                ps.setBoolean(4, water);
                ps.setBoolean(5, parking);
                ps.setBoolean(6, services);
                ps.setBoolean(7, rent);
                ps.setBigDecimal(8, amount);
                ps.setDate(9, Date.valueOf(deadline));
                ps.setString(10, statusCombo.getSelectionModel().getSelectedItem() != null ? statusCombo.getSelectionModel().getSelectedItem() : "not_payed");
                // payment_date can be null (not set at creation)
                if (paymentDatePicker.getValue() != null) ps.setDate(11, Date.valueOf(paymentDatePicker.getValue()));
                else ps.setNull(11, Types.DATE);
                ps.setDate(12, Date.valueOf(LocalDate.now())); // creation date
                int changed = ps.executeUpdate();
                setStatus("Thêm thành công (" + changed + ").");
            }
            loadData();
            clearForm();
        } catch (SQLException ex) {
            setStatus("Lỗi khi thêm: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleUpdate() {
        FeeRecord sel = feeTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            setStatus("Chưa chọn bản ghi để cập nhật.");
            return;
        }
        int id = sel.getId();

        String residentIdText = residentIdField.getText().trim();
        String residentName = residentNameField.getText().trim();
        String feeType = feeTypeCombo.getSelectionModel().getSelectedItem();
        String amountText = amountField.getText().trim();
        LocalDate deadline = deadlinePicker.getValue();

        if (residentIdText.isEmpty() || residentName.isEmpty() || amountText.isEmpty() || deadline == null) {
            setStatus("Vui lòng điền đầy đủ: ID cư dân, tên, số tiền, hạn thanh toán.");
            return;
        }
        int residentId;
        BigDecimal amount;
        try {
            residentId = Integer.parseInt(residentIdText);
            amount = new BigDecimal(amountText);
        } catch (NumberFormatException e) {
            setStatus("ID cư dân hoặc số tiền không hợp lệ.");
            return;
        }

        boolean electric=false, water=false, parking=false, services=false, rent=false;
        switch (feeType) {
            case "Tiền gửi xe": parking = true; break;
            case "Tiền thuê nhà": rent = true; break;
            case "Phí dịch vụ": services = true; break;
            case "Tiền điện": electric = true; break;
            case "Tiền nước": water = true; break;
        }

        try (Connection conn = getConnection()) {
            String sql = "UPDATE fees_and_payments SET assigned_to_resident_id=?, resident_name=?, electric_type=?, water_type=?, parking_type=?, services_type=?, rent_type=?, payment_amount=?, deadline=?, payment_status=?, payment_date=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, residentId);
                ps.setString(2, residentName);
                ps.setBoolean(3, electric);
                ps.setBoolean(4, water);
                ps.setBoolean(5, parking);
                ps.setBoolean(6, services);
                ps.setBoolean(7, rent);
                ps.setBigDecimal(8, amount);
                ps.setDate(9, Date.valueOf(deadline));
                ps.setString(10, statusCombo.getSelectionModel().getSelectedItem() != null ? statusCombo.getSelectionModel().getSelectedItem() : "not_payed");
                if (paymentDatePicker.getValue() != null) ps.setDate(11, Date.valueOf(paymentDatePicker.getValue()));
                else ps.setNull(11, Types.DATE);
                ps.setInt(12, id);
                int changed = ps.executeUpdate();
                setStatus("Cập nhật thành công (" + changed + ").");
            }
            loadData();
            clearForm();
        } catch (SQLException ex) {
            setStatus("Lỗi khi cập nhật: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        FeeRecord sel = feeTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            setStatus("Chưa chọn bản ghi để xóa.");
            return;
        }
        int id = sel.getId();

        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM fees_and_payments WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, id);
                int changed = ps.executeUpdate();
                setStatus("Xóa thành công (" + changed + ").");
            }
            loadData();
            clearForm();
        } catch (SQLException ex) {
            setStatus("Lỗi khi xóa: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Data model
    public static class FeeRecord {
        private final IntegerProperty id = new SimpleIntegerProperty();
        private final IntegerProperty residentId = new SimpleIntegerProperty();
        private final StringProperty residentName = new SimpleStringProperty();
        private final BooleanProperty electric = new SimpleBooleanProperty();
        private final BooleanProperty water = new SimpleBooleanProperty();
        private final BooleanProperty parking = new SimpleBooleanProperty();
        private final BooleanProperty services = new SimpleBooleanProperty();
        private final BooleanProperty rent = new SimpleBooleanProperty();
        private final ObjectProperty<BigDecimal> amount = new SimpleObjectProperty<>(BigDecimal.ZERO);
        private final ObjectProperty<LocalDate> deadline = new SimpleObjectProperty<>();
        private final StringProperty status = new SimpleStringProperty();
        private final ObjectProperty<LocalDate> paymentDate = new SimpleObjectProperty<>();
        private final ObjectProperty<LocalDate> creationDate = new SimpleObjectProperty<>();

        public FeeRecord(int id, int residentId, String residentName, boolean electric, boolean water, boolean parking, boolean services, boolean rent, BigDecimal amount, LocalDate deadline, String status, LocalDate paymentDate, LocalDate creationDate) {
            this.id.set(id);
            this.residentId.set(residentId);
            this.residentName.set(residentName);
            this.electric.set(electric);
            this.water.set(water);
            this.parking.set(parking);
            this.services.set(services);
            this.rent.set(rent);
            this.amount.set(amount != null ? amount : BigDecimal.ZERO);
            this.deadline.set(deadline);
            this.status.set(status);
            this.paymentDate.set(paymentDate);
            this.creationDate.set(creationDate);
        }

        public int getId() { return id.get(); }
        public IntegerProperty idProperty() { return id; }

        public int getResidentId() { return residentId.get(); }
        public IntegerProperty residentIdProperty() { return residentId; }

        public String getResidentName() { return residentName.get(); }
        public StringProperty residentNameProperty() { return residentName; }

        public boolean isElectric() { return electric.get(); }
        public BooleanProperty electricProperty() { return electric; }

        public boolean isWater() { return water.get(); }
        public BooleanProperty waterProperty() { return water; }

        public boolean isParking() { return parking.get(); }
        public BooleanProperty parkingProperty() { return parking; }

        public boolean isServices() { return services.get(); }
        public BooleanProperty servicesProperty() { return services; }

        public boolean isRent() { return rent.get(); }
        public BooleanProperty rentProperty() { return rent; }

        public BigDecimal getAmount() { return amount.get(); }
        public ObjectProperty<BigDecimal> amountProperty() { return amount; }

        public StringProperty amountStringProperty() { return new SimpleStringProperty(getAmount().toPlainString()); }

        public LocalDate getDeadline() { return deadline.get(); }
        public ObjectProperty<LocalDate> deadlineProperty() { return deadline; }

        public String getStatus() { return status.get(); }
        public StringProperty statusProperty() { return status; }

        public LocalDate getPaymentDate() { return paymentDate.get(); }
        public ObjectProperty<LocalDate> paymentDateProperty() { return paymentDate; }

        public LocalDate getCreationDate() { return creationDate.get(); }
        public ObjectProperty<LocalDate> creationDateProperty() { return creationDate; }
    }
}
