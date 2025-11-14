package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.model.BMSSystem;
import com.example.quanlytoanhanhom4.service.BMSService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class BMSController implements Initializable {
    
    @FXML private TableView<BMSSystem> systemTable;
    @FXML private TableColumn<BMSSystem, String> colSystemType;
    @FXML private TableColumn<BMSSystem, String> colSystemName;
    @FXML private TableColumn<BMSSystem, String> colLocation;
    @FXML private TableColumn<BMSSystem, String> colStatus;
    @FXML private TableColumn<BMSSystem, Double> colValue;
    @FXML private TableColumn<BMSSystem, String> colUnit;
    
    @FXML private ComboBox<String> filterTypeCombo;
    @FXML private TextField systemNameField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> systemTypeCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextField valueField;
    @FXML private TextField unitField;
    @FXML private TextArea descriptionArea;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;
    
    private ObservableList<BMSSystem> systems;
    private BMSSystem selectedSystem;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        initializeComboBoxes();
        loadSystems();
        
        systemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedSystem = newSelection;
                loadSystemToForm(newSelection);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
                addButton.setDisable(true);
            }
        });
    }
    
    private void initializeTable() {
        colSystemType.setCellValueFactory(new PropertyValueFactory<>("systemType"));
        colSystemName.setCellValueFactory(new PropertyValueFactory<>("systemName"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colValue.setCellValueFactory(new PropertyValueFactory<>("currentValue"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        
        systems = FXCollections.observableArrayList();
        systemTable.setItems(systems);
    }
    
    private void initializeComboBoxes() {
        ObservableList<String> systemTypes = FXCollections.observableArrayList(
            "ĐIỆN", "NƯỚC", "HVAC", "PCCC", "AN_NINH", "CHIEU_SANG"
        );
        systemTypeCombo.setItems(systemTypes);
        filterTypeCombo.setItems(systemTypes);
        filterTypeCombo.getItems().add(0, "TẤT CẢ");
        filterTypeCombo.setValue("TẤT CẢ");
        
        ObservableList<String> statuses = FXCollections.observableArrayList(
            "NORMAL", "WARNING", "ERROR", "MAINTENANCE"
        );
        statusCombo.setItems(statuses);
    }
    
    private void loadSystems() {
        systems.clear();
        String filterType = filterTypeCombo.getValue();
        
        if (filterType == null || filterType.equals("TẤT CẢ")) {
            systems.addAll(BMSService.getAllSystems());
        } else {
            systems.addAll(BMSService.getSystemsByType(filterType));
        }
    }
    
    private void loadSystemToForm(BMSSystem system) {
        systemTypeCombo.setValue(system.getSystemType());
        systemNameField.setText(system.getSystemName());
        locationField.setText(system.getLocation());
        statusCombo.setValue(system.getStatus());
        valueField.setText(system.getCurrentValue() != null ? system.getCurrentValue().toString() : "");
        unitField.setText(system.getUnit());
        descriptionArea.setText(system.getDescription());
    }
    
    @FXML
    private void handleFilter() {
        loadSystems();
    }
    
    @FXML
    private void handleAdd() {
        if (validateInput()) {
            BMSSystem system = new BMSSystem();
            system.setSystemType(systemTypeCombo.getValue());
            system.setSystemName(systemNameField.getText().trim());
            system.setLocation(locationField.getText().trim());
            system.setStatus(statusCombo.getValue());
            system.setUnit(unitField.getText().trim());
            system.setDescription(descriptionArea.getText().trim());
            
            try {
                system.setCurrentValue(valueField.getText().trim().isEmpty() ? null : Double.parseDouble(valueField.getText().trim()));
            } catch (NumberFormatException e) {
                showAlert("Lỗi", "Giá trị không hợp lệ!", Alert.AlertType.ERROR);
                return;
            }
            
            if (BMSService.addSystem(system)) {
                statusLabel.setText("✅ Thêm hệ thống thành công!");
                clearForm();
                loadSystems();
            } else {
                showAlert("Lỗi", "Không thể thêm hệ thống!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (selectedSystem == null) {
            showAlert("Cảnh báo", "Vui lòng chọn hệ thống cần cập nhật!", Alert.AlertType.WARNING);
            return;
        }
        
        if (validateInput()) {
            selectedSystem.setSystemType(systemTypeCombo.getValue());
            selectedSystem.setSystemName(systemNameField.getText().trim());
            selectedSystem.setLocation(locationField.getText().trim());
            selectedSystem.setStatus(statusCombo.getValue());
            selectedSystem.setUnit(unitField.getText().trim());
            selectedSystem.setDescription(descriptionArea.getText().trim());
            
            try {
                selectedSystem.setCurrentValue(valueField.getText().trim().isEmpty() ? null : Double.parseDouble(valueField.getText().trim()));
            } catch (NumberFormatException e) {
                showAlert("Lỗi", "Giá trị không hợp lệ!", Alert.AlertType.ERROR);
                return;
            }
            
            if (BMSService.updateSystem(selectedSystem)) {
                statusLabel.setText("✅ Cập nhật hệ thống thành công!");
                clearForm();
                loadSystems();
                selectedSystem = null;
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
                addButton.setDisable(false);
            } else {
                showAlert("Lỗi", "Không thể cập nhật hệ thống!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleDelete() {
        if (selectedSystem == null) {
            showAlert("Cảnh báo", "Vui lòng chọn hệ thống cần xóa!", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Xóa hệ thống");
        confirm.setContentText("Bạn có chắc chắn muốn xóa hệ thống: " + selectedSystem.getSystemName() + "?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (BMSService.deleteSystem(selectedSystem.getId())) {
                statusLabel.setText("✅ Xóa hệ thống thành công!");
                clearForm();
                loadSystems();
                selectedSystem = null;
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
                addButton.setDisable(false);
            } else {
                showAlert("Lỗi", "Không thể xóa hệ thống!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleClear() {
        clearForm();
        selectedSystem = null;
        systemTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
    }
    
    @FXML
    private void handleBack() {
        Stage stage = (Stage) systemTable.getScene().getWindow();
        stage.close();
    }
    
    private void clearForm() {
        systemTypeCombo.setValue(null);
        systemNameField.clear();
        locationField.clear();
        statusCombo.setValue("NORMAL");
        valueField.clear();
        unitField.clear();
        descriptionArea.clear();
        statusLabel.setText("");
    }
    
    private boolean validateInput() {
        if (systemTypeCombo.getValue() == null || systemTypeCombo.getValue().isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn loại hệ thống!", Alert.AlertType.ERROR);
            return false;
        }
        if (systemNameField.getText().trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập tên hệ thống!", Alert.AlertType.ERROR);
            return false;
        }
        if (statusCombo.getValue() == null || statusCombo.getValue().isEmpty()) {
            showAlert("Lỗi", "Vui lòng chọn trạng thái!", Alert.AlertType.ERROR);
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






