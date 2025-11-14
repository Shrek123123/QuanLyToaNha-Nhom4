package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.model.Maintenance;
import com.example.quanlytoanhanhom4.service.MaintenanceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MaintenanceController implements Initializable {
    
    @FXML private TableView<Maintenance> maintenanceTable;
    @FXML private TableColumn<Maintenance, String> colSystemType;
    @FXML private TableColumn<Maintenance, String> colMaintenanceType;
    @FXML private TableColumn<Maintenance, String> colDescription;
    @FXML private TableColumn<Maintenance, LocalDate> colScheduledDate;
    @FXML private TableColumn<Maintenance, String> colStatus;
    @FXML private TableColumn<Maintenance, String> colPriority;
    
    @FXML private ComboBox<String> filterStatusCombo;
    @FXML private ComboBox<String> systemTypeCombo;
    @FXML private ComboBox<String> maintenanceTypeCombo;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker scheduledDatePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextArea notesArea;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button completeButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;
    
    private ObservableList<Maintenance> maintenances;
    private Maintenance selectedMaintenance;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        initializeComboBoxes();
        loadMaintenances();
        
        maintenanceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMaintenance = newSelection;
                loadMaintenanceToForm(newSelection);
                updateButton.setDisable(false);
                completeButton.setDisable(false);
                deleteButton.setDisable(false);
                addButton.setDisable(true);
            }
        });
    }
    
    private void initializeTable() {
        colSystemType.setCellValueFactory(new PropertyValueFactory<>("systemType"));
        colMaintenanceType.setCellValueFactory(new PropertyValueFactory<>("maintenanceType"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colScheduledDate.setCellValueFactory(new PropertyValueFactory<>("scheduledDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        
        maintenances = FXCollections.observableArrayList();
        maintenanceTable.setItems(maintenances);
    }
    
    private void initializeComboBoxes() {
        ObservableList<String> systemTypes = FXCollections.observableArrayList(
            "ĐIỆN", "NƯỚC", "HVAC", "PCCC", "AN_NINH", "CHIEU_SANG"
        );
        systemTypeCombo.setItems(systemTypes);
        
        ObservableList<String> maintenanceTypes = FXCollections.observableArrayList(
            "PREVENTIVE", "CORRECTIVE", "EMERGENCY"
        );
        maintenanceTypeCombo.setItems(maintenanceTypes);
        
        ObservableList<String> statuses = FXCollections.observableArrayList(
            "PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"
        );
        statusCombo.setItems(statuses);
        filterStatusCombo.setItems(statuses);
        filterStatusCombo.getItems().add(0, "TẤT CẢ");
        filterStatusCombo.setValue("TẤT CẢ");
        
        ObservableList<String> priorities = FXCollections.observableArrayList(
            "LOW", "MEDIUM", "HIGH", "URGENT"
        );
        priorityCombo.setItems(priorities);
    }
    
    private void loadMaintenances() {
        maintenances.clear();
        String filterStatus = filterStatusCombo.getValue();
        
        if (filterStatus == null || filterStatus.equals("TẤT CẢ")) {
            maintenances.addAll(MaintenanceService.getAllMaintenances());
        } else {
            maintenances.addAll(MaintenanceService.getMaintenancesByStatus(filterStatus));
        }
    }
    
    private void loadMaintenanceToForm(Maintenance maintenance) {
        systemTypeCombo.setValue(maintenance.getSystemType());
        maintenanceTypeCombo.setValue(maintenance.getMaintenanceType());
        descriptionArea.setText(maintenance.getDescription());
        scheduledDatePicker.setValue(maintenance.getScheduledDate());
        statusCombo.setValue(maintenance.getStatus());
        priorityCombo.setValue(maintenance.getPriority());
        notesArea.setText(maintenance.getNotes());
    }
    
    @FXML
    private void handleFilter() {
        loadMaintenances();
    }
    
    @FXML
    private void handleAdd() {
        if (validateInput()) {
            Maintenance maintenance = new Maintenance();
            maintenance.setSystemType(systemTypeCombo.getValue());
            maintenance.setMaintenanceType(maintenanceTypeCombo.getValue());
            maintenance.setDescription(descriptionArea.getText().trim());
            maintenance.setScheduledDate(scheduledDatePicker.getValue());
            maintenance.setPriority(priorityCombo.getValue());
            maintenance.setNotes(notesArea.getText().trim());
            
            if (MaintenanceService.addMaintenance(maintenance)) {
                statusLabel.setText("✅ Thêm bảo trì thành công!");
                clearForm();
                loadMaintenances();
            } else {
                showAlert("Lỗi", "Không thể thêm bảo trì!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (selectedMaintenance == null) return;
        
        if (validateInput()) {
            selectedMaintenance.setSystemType(systemTypeCombo.getValue());
            selectedMaintenance.setMaintenanceType(maintenanceTypeCombo.getValue());
            selectedMaintenance.setDescription(descriptionArea.getText().trim());
            selectedMaintenance.setScheduledDate(scheduledDatePicker.getValue());
            selectedMaintenance.setStatus(statusCombo.getValue());
            selectedMaintenance.setPriority(priorityCombo.getValue());
            selectedMaintenance.setNotes(notesArea.getText().trim());
            
            if (MaintenanceService.updateMaintenance(selectedMaintenance)) {
                statusLabel.setText("✅ Cập nhật bảo trì thành công!");
                clearForm();
                loadMaintenances();
                resetSelection();
            } else {
                showAlert("Lỗi", "Không thể cập nhật bảo trì!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleComplete() {
        if (selectedMaintenance == null) return;
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Hoàn thành bảo trì");
        dialog.setHeaderText("Nhập ghi chú hoàn thành");
        dialog.setContentText("Ghi chú:");
        
        dialog.showAndWait().ifPresent(notes -> {
            if (MaintenanceService.completeMaintenance(selectedMaintenance.getId(), LocalDate.now(), notes)) {
                statusLabel.setText("✅ Hoàn thành bảo trì!");
                clearForm();
                loadMaintenances();
                resetSelection();
            }
        });
    }
    
    @FXML
    private void handleDelete() {
        if (selectedMaintenance == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Xóa bảo trì");
        confirm.setContentText("Bạn có chắc chắn muốn xóa bảo trì này?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (MaintenanceService.deleteMaintenance(selectedMaintenance.getId())) {
                statusLabel.setText("✅ Xóa bảo trì thành công!");
                clearForm();
                loadMaintenances();
                resetSelection();
            }
        }
    }
    
    @FXML
    private void handleClear() {
        clearForm();
        resetSelection();
    }
    
    @FXML
    private void handleBack() {
        Stage stage = (Stage) maintenanceTable.getScene().getWindow();
        stage.close();
    }
    
    private void clearForm() {
        systemTypeCombo.setValue(null);
        maintenanceTypeCombo.setValue(null);
        descriptionArea.clear();
        scheduledDatePicker.setValue(null);
        statusCombo.setValue("PENDING");
        priorityCombo.setValue("MEDIUM");
        notesArea.clear();
        statusLabel.setText("");
    }
    
    private void resetSelection() {
        selectedMaintenance = null;
        maintenanceTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        completeButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
    }
    
    private boolean validateInput() {
        if (systemTypeCombo.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn loại hệ thống!", Alert.AlertType.ERROR);
            return false;
        }
        if (maintenanceTypeCombo.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn loại bảo trì!", Alert.AlertType.ERROR);
            return false;
        }
        if (scheduledDatePicker.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn ngày lên lịch!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}






