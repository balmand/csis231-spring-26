package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.DepartmentDto;
import com.csis231.javafxclient.model.EmployeeDto;
import com.csis231.javafxclient.model.PagedResponseDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class DepartmentController {
    @FXML
    private TableView<DepartmentDto> departmentTable;
    @FXML
    private TableColumn<DepartmentDto, Long> idColumn;
    @FXML
    private TableColumn<DepartmentDto, String> nameColumn;
    @FXML
    private TableColumn<DepartmentDto, String> locationColumn;
    @FXML
    private TableColumn<DepartmentDto, Integer> employeeCountColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortFieldCombo;
    @FXML
    private ComboBox<String> sortDirCombo;
    @FXML
    private ComboBox<Integer> pageSizeCombo;
    @FXML
    private Button searchButton;
    @FXML
    private Button clearSearchButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button nextButton;
    @FXML
    private Label pageLabel;

    @FXML
    private TableView<EmployeeDto> employeeTable;
    @FXML
    private TableColumn<EmployeeDto, String> empFirstNameColumn;
    @FXML
    private TableColumn<EmployeeDto, String> empLastNameColumn;
    @FXML
    private TableColumn<EmployeeDto, String> empEmailColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;

    @FXML
    private Button createButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Label statusLabel;

    private ApiClient apiClient;
    private ObservableList<DepartmentDto> departmentList;
    private ObservableList<EmployeeDto> employeeList;

    private int page = 0;
    private int totalPages = 0;

    public DepartmentController() {
        this.apiClient = new ApiClient();
        this.departmentList = FXCollections.observableArrayList();
        this.employeeList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Setup department table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        employeeCountColumn.setCellValueFactory(cellData -> {
            DepartmentDto dept = cellData.getValue();
            int count = dept.getEmployees() != null ? dept.getEmployees().size() : 0;
            return new SimpleIntegerProperty(count).asObject();
        });

        // Setup employee table columns
        empFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        empLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        empEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        departmentTable.setItems(departmentList);
        employeeTable.setItems(employeeList);

        // Department selection listener
        departmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadDepartmentToForm(newSelection);
                loadDepartmentEmployees(newSelection);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                clearForm();
                employeeList.clear();
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        // Load data
        setupSearchControls();
        runDepartmentSearch();
        refreshDepartments();
    }

    @FXML
    private void createDepartment() {
        try {
            DepartmentDto department = new DepartmentDto();
            department.setName(nameField.getText());
            department.setLocation(locationField.getText());

            apiClient.createDepartment(department);
            statusLabel.setText("Department created successfully!");
            refreshDepartments();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void updateDepartment() {
        DepartmentDto selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select a department to update");
            return;
        }

        try {
            selected.setName(nameField.getText());
            selected.setLocation(locationField.getText());

            apiClient.updateDepartment(selected.getId(), selected);
            statusLabel.setText("Department updated successfully!");
            refreshDepartments();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteDepartment() {
        DepartmentDto selected = departmentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select a department to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Department");
        alert.setContentText("Are you sure you want to delete department: " + selected.getName() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                apiClient.deleteDepartment(selected.getId());
                statusLabel.setText("Department deleted successfully!");
                refreshDepartments();
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void searchDepartments() {
        page = 0;
        runDepartmentSearch();
    }

    @FXML
    private void clearSearch() {
        if (searchField != null) {
            searchField.clear();
        }
        if (sortFieldCombo != null) {
            sortFieldCombo.getSelectionModel().select("name");
        }
        if (sortDirCombo != null) {
            sortDirCombo.getSelectionModel().select("asc");
        }
        if (pageSizeCombo != null) {
            pageSizeCombo.getSelectionModel().select(Integer.valueOf(25));
        }
        page = 0;
        runDepartmentSearch();
    }

    @FXML
    private void prevPage() {
        if (page > 0) {
            page--;
            runDepartmentSearch();
        }
    }

    @FXML
    private void nextPage() {
        if (page + 1 < totalPages) {
            page++;
            runDepartmentSearch();
        }
    }

    private void setupSearchControls() {
        if (sortFieldCombo != null) {
            sortFieldCombo.setItems(FXCollections.observableArrayList("name", "location"));
            sortFieldCombo.getSelectionModel().select("name");
        }
        if (sortDirCombo != null) {
            sortDirCombo.setItems(FXCollections.observableArrayList("asc", "desc"));
            sortDirCombo.getSelectionModel().select("asc");
        }
        if (pageSizeCombo != null) {
            pageSizeCombo.setItems(FXCollections.observableArrayList(10, 25, 50, 100, 500, 1000));
            pageSizeCombo.getSelectionModel().select(Integer.valueOf(25));
        }
        updatePagingControls();
    }

    private void setSearchLoading(boolean loading) {
        if (searchButton != null) searchButton.setDisable(loading);
        if (clearSearchButton != null) clearSearchButton.setDisable(loading);
        if (prevButton != null) prevButton.setDisable(loading || page <= 0);
        if (nextButton != null) nextButton.setDisable(loading || page + 1 >= totalPages);
    }

    private void updatePagingControls() {
        String label = totalPages == 0 ? "Page 0 of 0" : "Page " + (page + 1) + " of " + totalPages;
        if (pageLabel != null) pageLabel.setText(label);
        if (prevButton != null) prevButton.setDisable(page <= 0);
        if (nextButton != null) nextButton.setDisable(page + 1 >= totalPages);
    }

    private void runDepartmentSearch() {
        String q = searchField == null ? "" : searchField.getText();
        String sortField = sortFieldCombo == null ? "name" : sortFieldCombo.getValue();
        String sortDir = sortDirCombo == null ? "asc" : sortDirCombo.getValue();
        Integer size = pageSizeCombo == null ? 25 : pageSizeCombo.getValue();

        Task<PagedResponseDto<DepartmentDto>> task = new Task<>() {
            @Override
            protected PagedResponseDto<DepartmentDto> call() throws Exception {
                return apiClient.searchDepartments(q, page, size, sortField, sortDir);
            }
        };

        setSearchLoading(true);
        statusLabel.setText("Loading departments...");

        task.setOnSucceeded(evt -> {
            PagedResponseDto<DepartmentDto> resp = task.getValue();
            List<DepartmentDto> newItems = resp.getItems();
            departmentList.setAll(newItems == null ? List.of() : newItems);

            totalPages = resp.getTotalPages();
            page = resp.getPage();
            updatePagingControls();
            statusLabel.setText("Departments loaded: " + departmentList.size() + " (total " + resp.getTotalItems() + ")");
            setSearchLoading(false);
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            statusLabel.setText("Error loading departments: " + (ex == null ? "unknown error" : ex.getMessage()));
            setSearchLoading(false);
        });

        Thread thread = new Thread(task, "departments-search");
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void refreshDepartments() {
        try {
            List<DepartmentDto> departments = apiClient.getAllDepartments();
            departmentList.clear();
            departmentList.addAll(departments);
            statusLabel.setText("Departments loaded: " + departments.size());
        } catch (Exception e) {
            statusLabel.setText("Error loading departments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDepartmentToForm(DepartmentDto department) {
        nameField.setText(department.getName());
        locationField.setText(department.getLocation());
    }

    private void loadDepartmentEmployees(DepartmentDto department) {
        employeeList.clear();
        if (department.getEmployees() != null) {
            employeeList.addAll(department.getEmployees());
        }
    }

    private void clearForm() {
        nameField.clear();
        locationField.clear();
        departmentTable.getSelectionModel().clearSelection();
        employeeList.clear();
    }
}
