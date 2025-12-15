package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.model.Resident;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

public class ResidentController {

    /* ===== TABLE ===== */
    @FXML private TableView<Resident> residentTable;
    @FXML private TableColumn<Resident, String> colFullName;
    @FXML private TableColumn<Resident, String> colGender;
    @FXML private TableColumn<Resident, String> colApartment;
    @FXML private TableColumn<Resident, String> colOwner;
    @FXML private TableColumn<Resident, String> colPhone;
    @FXML private TableColumn<Resident, String> colEmail;
    @FXML private TableColumn<Resident, String> colStatus;

    /* ===== FORM ===== */
    @FXML private TextField fullNameField, phoneField, emailField, identityCardField;
    @FXML private TextField apartmentField;
    @FXML private TextField emergencyContactField, emergencyPhoneField;
    @FXML private TextArea addressArea, notesArea;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private ComboBox<String> genderCombo, statusCombo, ownerCombo, filterStatusCombo;

    @FXML private Button addButton, updateButton, deleteButton;
    @FXML private Label statusLabel;

    private final ObservableList<Resident> residentList = FXCollections.observableArrayList();
    private Resident selectedResident;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/quanlytoanha";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    /* ===== INIT ===== */
    public void initialize() {
        setupTable();
        setupCombos();
        loadResidents();

        residentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> onSelectResident(newVal)
        );
    }

    private void setupTable() {
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colApartment.setCellValueFactory(new PropertyValueFactory<>("apartment"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerText"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        residentTable.setItems(residentList);
    }

    private void setupCombos() {
        genderCombo.setItems(FXCollections.observableArrayList("Nam", "Nữ", "Khác"));
        statusCombo.setItems(FXCollections.observableArrayList(
                "Vẫn định cư", "Đã chuyển", "Sắp chuyển", "Thuê", "Khách"
        ));
        ownerCombo.setItems(FXCollections.observableArrayList(
                "Là chủ căn hộ", "Không phải chủ hộ"
        ));
        ownerCombo.getSelectionModel().selectFirst();

        filterStatusCombo.setItems(statusCombo.getItems());
    }

    /* ===== LOAD ===== */
    private void loadResidents() {
        residentList.clear();
        String sql = "SELECT * FROM resident";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Resident r = new Resident();
                r.setResidentId(rs.getInt("resident_id"));
                r.setUserId((Integer) rs.getObject("user_id"));
                r.setFullName(rs.getString("full_name"));
                r.setApartment(rs.getString("apartment"));
                r.setOwner(rs.getInt("is_owner") == 1);
                r.setPhone(rs.getString("phone"));
                r.setEmail(rs.getString("email"));
                r.setIdentityCard(rs.getString("identity_card"));

                Date dob = rs.getDate("date_of_birth");
                if (dob != null) r.setDateOfBirth(dob.toLocalDate());

                r.setGender(rs.getString("gender"));
                r.setAddress(rs.getString("address"));
                r.setEmergencyContact(rs.getString("emergency_contact"));
                r.setEmergencyPhone(rs.getString("emergency_phone"));
                r.setStatus(rs.getString("status"));
                r.setNotes(rs.getString("notes"));
                r.setBalance(rs.getDouble("balance"));

                residentList.add(r);
            }

            statusLabel.setText("Tổng: " + residentList.size() + " cư dân");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ===== CRUD ===== */
    @FXML
    public void handleAdd(ActionEvent e) {
        String sql = """
                INSERT INTO resident
                (full_name, apartment, is_owner, phone, email, identity_card,
                 date_of_birth, gender, address, emergency_contact,
                 emergency_phone, status, notes)
                VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, false);
            ps.executeUpdate();

            loadResidents();
            clearForm();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleUpdate(ActionEvent e) {
        if (selectedResident == null) return;

        String sql = """
                UPDATE resident SET
                full_name=?, apartment=?, is_owner=?, phone=?, email=?,
                identity_card=?, date_of_birth=?, gender=?, address=?,
                emergency_contact=?, emergency_phone=?, status=?, notes=?
                WHERE resident_id=?
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, true);
            ps.executeUpdate();

            loadResidents();
            clearForm();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleDelete(ActionEvent e) {
        if (selectedResident == null) return;

        String sql = "DELETE FROM resident WHERE resident_id=?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, selectedResident.getResidentId());
            ps.executeUpdate();

            loadResidents();
            clearForm();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /* ===== HELPERS ===== */
    private void fillStatement(PreparedStatement ps, boolean isUpdate) throws SQLException {
        ps.setString(1, fullNameField.getText());
        ps.setString(2, apartmentField.getText());
        ps.setInt(3, ownerCombo.getValue().equals("Là chủ căn hộ") ? 1 : 0);
        ps.setString(4, phoneField.getText());
        ps.setString(5, emailField.getText());
        ps.setString(6, identityCardField.getText());

        LocalDate dob = dateOfBirthPicker.getValue();
        ps.setDate(7, dob != null ? Date.valueOf(dob) : null);

        ps.setString(8, genderCombo.getValue());
        ps.setString(9, addressArea.getText());
        ps.setString(10, emergencyContactField.getText());
        ps.setString(11, emergencyPhoneField.getText());
        ps.setString(12, statusCombo.getValue());
        ps.setString(13, notesArea.getText());

        if (isUpdate) {
            ps.setInt(14, selectedResident.getResidentId());
        }
    }

    private void onSelectResident(Resident r) {
        selectedResident = r;
        if (r == null) return;

        fullNameField.setText(r.getFullName());
        apartmentField.setText(r.getApartment());
        phoneField.setText(r.getPhone());
        emailField.setText(r.getEmail());
        identityCardField.setText(r.getIdentityCard());
        dateOfBirthPicker.setValue(r.getDateOfBirth());
        genderCombo.setValue(r.getGender());
        addressArea.setText(r.getAddress());
        emergencyContactField.setText(r.getEmergencyContact());
        emergencyPhoneField.setText(r.getEmergencyPhone());
        statusCombo.setValue(r.getStatus());
        notesArea.setText(r.getNotes());
        ownerCombo.setValue(r.isOwner() ? "Là chủ căn hộ" : "Không phải chủ hộ");

        updateButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void clearForm() {
        residentTable.getSelectionModel().clearSelection();
        fullNameField.clear();
        apartmentField.clear();
        phoneField.clear();
        emailField.clear();
        identityCardField.clear();
        dateOfBirthPicker.setValue(null);
        genderCombo.setValue(null);
        statusCombo.setValue(null);
        addressArea.clear();
        emergencyContactField.clear();
        emergencyPhoneField.clear();
        notesArea.clear();

        updateButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    /* ===== TOP BAR ===== */
    @FXML
    public void handleFilter(ActionEvent e) {
        loadResidents();
    }

    @FXML
    public void handleBack(ActionEvent e) {
        try {
            Stage stage = (Stage) residentTable.getScene().getWindow();
            stage.setScene(new Scene(
                    FXMLLoader.load(getClass().getResource("/com/example/quanlytoanhanhom4/fxml/admin_main.fxml"))
            ));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
