package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.model.CustomerRequest;
import com.example.quanlytoanhanhom4.service.CustomerRequestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {
    
    @FXML private TableView<CustomerRequest> requestTable;
    @FXML private TableColumn<CustomerRequest, String> colRequestType;
    @FXML private TableColumn<CustomerRequest, String> colTitle;
    @FXML private TableColumn<CustomerRequest, String> colContent;
    @FXML private TableColumn<CustomerRequest, String> colStatus;
    @FXML private TableColumn<CustomerRequest, String> colPriority;
    
    @FXML private ComboBox<String> filterStatusCombo;
    @FXML private TextField residentIdField;
    @FXML private ComboBox<String> requestTypeCombo;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextArea resolutionArea;
    @FXML private Spinner<Integer> ratingSpinner;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button resolveButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;
    
    private ObservableList<CustomerRequest> requests;
    private CustomerRequest selectedRequest;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        initializeComboBoxes();
        initializeSpinner();
        loadRequests();
        
        requestTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedRequest = newSelection;
                loadRequestToForm(newSelection);
                updateButton.setDisable(false);
                resolveButton.setDisable(false);
                deleteButton.setDisable(false);
                addButton.setDisable(true);
            }
        });
    }
    
    private void initializeTable() {
        colRequestType.setCellValueFactory(new PropertyValueFactory<>("requestType"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        
        requests = FXCollections.observableArrayList();
        requestTable.setItems(requests);
    }
    
    private void initializeComboBoxes() {
        ObservableList<String> requestTypes = FXCollections.observableArrayList(
            "COMPLAINT", "REQUEST", "FEEDBACK", "EMERGENCY"
        );
        requestTypeCombo.setItems(requestTypes);
        
        ObservableList<String> statuses = FXCollections.observableArrayList(
            "PENDING", "IN_PROGRESS", "RESOLVED", "CLOSED"
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
    
    private void initializeSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 3);
        ratingSpinner.setValueFactory(valueFactory);
    }
    
    private void loadRequests() {
        requests.clear();
        String filterStatus = filterStatusCombo.getValue();
        
        if (filterStatus == null || filterStatus.equals("TẤT CẢ")) {
            requests.addAll(CustomerRequestService.getAllRequests());
        } else {
            requests.addAll(CustomerRequestService.getRequestsByStatus(filterStatus));
        }
    }
    
    private void loadRequestToForm(CustomerRequest request) {
        residentIdField.setText(String.valueOf(request.getResidentId()));
        requestTypeCombo.setValue(request.getRequestType());
        titleField.setText(request.getTitle());
        contentArea.setText(request.getContent());
        statusCombo.setValue(request.getStatus());
        priorityCombo.setValue(request.getPriority());
        resolutionArea.setText(request.getResolution());
        if (request.getSatisfactionRating() != null) {
            ratingSpinner.getValueFactory().setValue(request.getSatisfactionRating());
        }
    }
    
    @FXML
    private void handleFilter() {
        loadRequests();
    }
    
    @FXML
    private void handleAdd() {
        if (validateInput()) {
            CustomerRequest request = new CustomerRequest();
            try {
                request.setResidentId(Integer.parseInt(residentIdField.getText().trim()));
            } catch (NumberFormatException e) {
                showAlert("Lỗi", "ID cư dân không hợp lệ!", Alert.AlertType.ERROR);
                return;
            }
            request.setRequestType(requestTypeCombo.getValue());
            request.setTitle(titleField.getText().trim());
            request.setContent(contentArea.getText().trim());
            request.setPriority(priorityCombo.getValue());
            
            if (CustomerRequestService.addRequest(request)) {
                statusLabel.setText("✅ Thêm yêu cầu thành công!");
                clearForm();
                loadRequests();
            } else {
                showAlert("Lỗi", "Không thể thêm yêu cầu!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (selectedRequest == null) return;
        
        if (validateInput()) {
            selectedRequest.setRequestType(requestTypeCombo.getValue());
            selectedRequest.setTitle(titleField.getText().trim());
            selectedRequest.setContent(contentArea.getText().trim());
            selectedRequest.setStatus(statusCombo.getValue());
            selectedRequest.setPriority(priorityCombo.getValue());
            selectedRequest.setResolution(resolutionArea.getText().trim());
            
            if (CustomerRequestService.updateRequest(selectedRequest)) {
                statusLabel.setText("✅ Cập nhật yêu cầu thành công!");
                clearForm();
                loadRequests();
                resetSelection();
            } else {
                showAlert("Lỗi", "Không thể cập nhật yêu cầu!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleResolve() {
        if (selectedRequest == null) return;
        
        if (CustomerRequestService.resolveRequest(selectedRequest.getId(), 
                resolutionArea.getText().trim(), ratingSpinner.getValue())) {
            statusLabel.setText("✅ Đã giải quyết yêu cầu!");
            clearForm();
            loadRequests();
            resetSelection();
        }
    }
    
    @FXML
    private void handleDelete() {
        if (selectedRequest == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Xóa yêu cầu");
        confirm.setContentText("Bạn có chắc chắn muốn xóa yêu cầu này?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (CustomerRequestService.deleteRequest(selectedRequest.getId())) {
                statusLabel.setText("✅ Xóa yêu cầu thành công!");
                clearForm();
                loadRequests();
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
        Stage stage = (Stage) requestTable.getScene().getWindow();
        stage.close();
    }
    
    private void clearForm() {
        residentIdField.clear();
        requestTypeCombo.setValue(null);
        titleField.clear();
        contentArea.clear();
        statusCombo.setValue("PENDING");
        priorityCombo.setValue("MEDIUM");
        resolutionArea.clear();
        ratingSpinner.getValueFactory().setValue(3);
        statusLabel.setText("");
    }
    
    private void resetSelection() {
        selectedRequest = null;
        requestTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        resolveButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
    }
    
    private boolean validateInput() {
        if (residentIdField.getText().trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập ID cư dân!", Alert.AlertType.ERROR);
            return false;
        }
        if (requestTypeCombo.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn loại yêu cầu!", Alert.AlertType.ERROR);
            return false;
        }
        if (titleField.getText().trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập tiêu đề!", Alert.AlertType.ERROR);
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






