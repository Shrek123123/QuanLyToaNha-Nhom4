package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.model.Security;
import com.example.quanlytoanhanhom4.service.SecurityService;
import com.example.quanlytoanhanhom4.util.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SecurityController implements Initializable {
    
    @FXML private TableView<Security> securityTable;
    @FXML private TableColumn<Security, String> colIncidentType;
    @FXML private TableColumn<Security, String> colLocation;
    @FXML private TableColumn<Security, String> colDescription;
    @FXML private TableColumn<Security, String> colStatus;
    @FXML private TableColumn<Security, String> colPriority;
    
    @FXML private ComboBox<String> filterStatusCombo;
    @FXML private ComboBox<String> incidentTypeCombo;
    @FXML private TextField locationField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextArea resolutionArea;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button resolveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;
    
    private ObservableList<Security> incidents;
    private Security selectedIncident;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        initializeComboBoxes();
        loadIncidents();
        
        securityTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedIncident = newSelection;
                loadIncidentToForm(newSelection);
                updateButton.setDisable(false);
                resolveButton.setDisable(false);
                deleteButton.setDisable(false);
                addButton.setDisable(true);
            }
        });
    }
    
    private void initializeTable() {
        colIncidentType.setCellValueFactory(new PropertyValueFactory<>("incidentType"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        
        incidents = FXCollections.observableArrayList();
        securityTable.setItems(incidents);
    }
    
    private void initializeComboBoxes() {
        ObservableList<String> incidentTypes = FXCollections.observableArrayList(
            "CAMERA", "ACCESS_CONTROL", "EMERGENCY", "THEFT", "OTHER"
        );
        incidentTypeCombo.setItems(incidentTypes);
        
        ObservableList<String> statuses = FXCollections.observableArrayList(
            "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED"
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
    
    private void loadIncidents() {
        incidents.clear();
        String filterStatus = filterStatusCombo.getValue();
        
        if (filterStatus == null || filterStatus.equals("TẤT CẢ")) {
            incidents.addAll(SecurityService.getAllIncidents());
        } else {
            incidents.addAll(SecurityService.getIncidentsByStatus(filterStatus));
        }
    }
    
    private void loadIncidentToForm(Security incident) {
        incidentTypeCombo.setValue(incident.getIncidentType());
        locationField.setText(incident.getLocation());
        descriptionArea.setText(incident.getDescription());
        statusCombo.setValue(incident.getStatus());
        priorityCombo.setValue(incident.getPriority());
        resolutionArea.setText(incident.getResolution());
    }
    
    @FXML
    private void handleFilter() {
        loadIncidents();
    }
    
    @FXML
    private void handleAdd() {
        if (validateInput()) {
            Security incident = new Security();
            incident.setIncidentType(incidentTypeCombo.getValue());
            incident.setLocation(locationField.getText().trim());
            incident.setDescription(descriptionArea.getText().trim());
            incident.setPriority(priorityCombo.getValue());
            incident.setReportedBy(UserSession.getCurrentUserId() != null ? UserSession.getCurrentUserId() : 1);
            
            if (SecurityService.addIncident(incident)) {
                statusLabel.setText("✅ Thêm sự cố thành công!");
                clearForm();
                loadIncidents();
            } else {
                showAlert("Lỗi", "Không thể thêm sự cố!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (selectedIncident == null) return;
        
        if (validateInput()) {
            selectedIncident.setIncidentType(incidentTypeCombo.getValue());
            selectedIncident.setLocation(locationField.getText().trim());
            selectedIncident.setDescription(descriptionArea.getText().trim());
            selectedIncident.setStatus(statusCombo.getValue());
            selectedIncident.setPriority(priorityCombo.getValue());
            selectedIncident.setResolution(resolutionArea.getText().trim());
            
            if (SecurityService.updateIncident(selectedIncident)) {
                statusLabel.setText("✅ Cập nhật sự cố thành công!");
                clearForm();
                loadIncidents();
                resetSelection();
            } else {
                showAlert("Lỗi", "Không thể cập nhật sự cố!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleResolve() {
        if (selectedIncident == null) return;
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Giải quyết sự cố");
        dialog.setHeaderText("Nhập giải pháp");
        dialog.setContentText("Giải pháp:");
        
        dialog.showAndWait().ifPresent(resolution -> {
            if (SecurityService.resolveIncident(selectedIncident.getId(), resolution)) {
                statusLabel.setText("✅ Đã giải quyết sự cố!");
                clearForm();
                loadIncidents();
                resetSelection();
            }
        });
    }
    
    @FXML
    private void handleDelete() {
        if (selectedIncident == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Xóa sự cố");
        confirm.setContentText("Bạn có chắc chắn muốn xóa sự cố này?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (SecurityService.deleteIncident(selectedIncident.getId())) {
                statusLabel.setText("✅ Xóa sự cố thành công!");
                clearForm();
                loadIncidents();
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
        Stage stage = (Stage) securityTable.getScene().getWindow();
        stage.close();
    }
    
    private void clearForm() {
        incidentTypeCombo.setValue(null);
        locationField.clear();
        descriptionArea.clear();
        statusCombo.setValue("OPEN");
        priorityCombo.setValue("MEDIUM");
        resolutionArea.clear();
        statusLabel.setText("");
    }
    
    private void resetSelection() {
        selectedIncident = null;
        securityTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        resolveButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
    }
    
    private boolean validateInput() {
        if (incidentTypeCombo.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn loại sự cố!", Alert.AlertType.ERROR);
            return false;
        }
        if (locationField.getText().trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập vị trí!", Alert.AlertType.ERROR);
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






