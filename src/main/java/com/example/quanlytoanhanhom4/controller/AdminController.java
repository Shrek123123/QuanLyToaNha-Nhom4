package com.example.quanlytoanhanhom4.controller;

import com.example.quanlytoanhanhom4.model.AdminTask;
import com.example.quanlytoanhanhom4.service.AdminTaskService;
import com.example.quanlytoanhanhom4.util.UserSession;
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

public class AdminController implements Initializable {
    
    @FXML private TableView<AdminTask> taskTable;
    @FXML private TableColumn<AdminTask, String> colTaskType;
    @FXML private TableColumn<AdminTask, String> colTitle;
    @FXML private TableColumn<AdminTask, LocalDate> colDueDate;
    @FXML private TableColumn<AdminTask, String> colStatus;
    @FXML private TableColumn<AdminTask, String> colPriority;
    
    @FXML private ComboBox<String> filterStatusCombo;
    @FXML private ComboBox<String> taskTypeCombo;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dueDatePicker;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private TextArea notesArea;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button completeButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;
    
    private ObservableList<AdminTask> tasks;
    private AdminTask selectedTask;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeTable();
        initializeComboBoxes();
        loadTasks();
        
        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTask = newSelection;
                loadTaskToForm(newSelection);
                updateButton.setDisable(false);
                completeButton.setDisable(false);
                deleteButton.setDisable(false);
                addButton.setDisable(true);
            }
        });
    }
    
    private void initializeTable() {
        colTaskType.setCellValueFactory(new PropertyValueFactory<>("taskType"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        
        tasks = FXCollections.observableArrayList();
        taskTable.setItems(tasks);
    }
    
    private void initializeComboBoxes() {
        ObservableList<String> taskTypes = FXCollections.observableArrayList(
            "HR", "FINANCE", "DOCUMENT", "MEETING", "OTHER"
        );
        taskTypeCombo.setItems(taskTypes);
        
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
    
    private void loadTasks() {
        tasks.clear();
        String filterStatus = filterStatusCombo.getValue();
        
        if (filterStatus == null || filterStatus.equals("TẤT CẢ")) {
            tasks.addAll(AdminTaskService.getAllTasks());
        } else {
            tasks.addAll(AdminTaskService.getTasksByStatus(filterStatus));
        }
    }
    
    private void loadTaskToForm(AdminTask task) {
        taskTypeCombo.setValue(task.getTaskType());
        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());
        dueDatePicker.setValue(task.getDueDate());
        statusCombo.setValue(task.getStatus());
        priorityCombo.setValue(task.getPriority());
        notesArea.setText(task.getNotes());
    }
    
    @FXML
    private void handleFilter() {
        loadTasks();
    }
    
    @FXML
    private void handleAdd() {
        if (validateInput()) {
            AdminTask task = new AdminTask();
            task.setTaskType(taskTypeCombo.getValue());
            task.setTitle(titleField.getText().trim());
            task.setDescription(descriptionArea.getText().trim());
            task.setDueDate(dueDatePicker.getValue());
            task.setPriority(priorityCombo.getValue());
            task.setNotes(notesArea.getText().trim());
            task.setCreatedBy(UserSession.getCurrentUserId() != null ? UserSession.getCurrentUserId() : 1);
            
            if (AdminTaskService.addTask(task)) {
                statusLabel.setText("✅ Thêm nhiệm vụ thành công!");
                clearForm();
                loadTasks();
            } else {
                showAlert("Lỗi", "Không thể thêm nhiệm vụ!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (selectedTask == null) return;
        
        if (validateInput()) {
            selectedTask.setTaskType(taskTypeCombo.getValue());
            selectedTask.setTitle(titleField.getText().trim());
            selectedTask.setDescription(descriptionArea.getText().trim());
            selectedTask.setDueDate(dueDatePicker.getValue());
            selectedTask.setStatus(statusCombo.getValue());
            selectedTask.setPriority(priorityCombo.getValue());
            selectedTask.setNotes(notesArea.getText().trim());
            
            if (AdminTaskService.updateTask(selectedTask)) {
                statusLabel.setText("✅ Cập nhật nhiệm vụ thành công!");
                clearForm();
                loadTasks();
                resetSelection();
            } else {
                showAlert("Lỗi", "Không thể cập nhật nhiệm vụ!", Alert.AlertType.ERROR);
            }
        }
    }
    
    @FXML
    private void handleComplete() {
        if (selectedTask == null) return;
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Hoàn thành nhiệm vụ");
        dialog.setHeaderText("Nhập ghi chú hoàn thành");
        dialog.setContentText("Ghi chú:");
        
        dialog.showAndWait().ifPresent(notes -> {
            if (AdminTaskService.completeTask(selectedTask.getId(), LocalDate.now(), notes)) {
                statusLabel.setText("✅ Hoàn thành nhiệm vụ!");
                clearForm();
                loadTasks();
                resetSelection();
            }
        });
    }
    
    @FXML
    private void handleDelete() {
        if (selectedTask == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận");
        confirm.setHeaderText("Xóa nhiệm vụ");
        confirm.setContentText("Bạn có chắc chắn muốn xóa nhiệm vụ này?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (AdminTaskService.deleteTask(selectedTask.getId())) {
                statusLabel.setText("✅ Xóa nhiệm vụ thành công!");
                clearForm();
                loadTasks();
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
        Stage stage = (Stage) taskTable.getScene().getWindow();
        stage.close();
    }
    
    private void clearForm() {
        taskTypeCombo.setValue(null);
        titleField.clear();
        descriptionArea.clear();
        dueDatePicker.setValue(null);
        statusCombo.setValue("PENDING");
        priorityCombo.setValue("MEDIUM");
        notesArea.clear();
        statusLabel.setText("");
    }
    
    private void resetSelection() {
        selectedTask = null;
        taskTable.getSelectionModel().clearSelection();
        updateButton.setDisable(true);
        completeButton.setDisable(true);
        deleteButton.setDisable(true);
        addButton.setDisable(false);
    }
    
    private boolean validateInput() {
        if (taskTypeCombo.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn loại nhiệm vụ!", Alert.AlertType.ERROR);
            return false;
        }
        if (titleField.getText().trim().isEmpty()) {
            showAlert("Lỗi", "Vui lòng nhập tiêu đề!", Alert.AlertType.ERROR);
            return false;
        }
        if (dueDatePicker.getValue() == null) {
            showAlert("Lỗi", "Vui lòng chọn ngày hết hạn!", Alert.AlertType.ERROR);
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






