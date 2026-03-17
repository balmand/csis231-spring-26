package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.PagedResponseDto;
import com.csis231.javafxclient.model.UserDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UsersSearchController {

    @FXML private TextField queryField;
    @FXML private ComboBox<String> sortFieldCombo;
    @FXML private ComboBox<String> sortDirCombo;
    @FXML private ComboBox<Integer> pageSizeCombo;
    @FXML private Button searchButton;
    @FXML private Button clearButton;
    @FXML private TableView<UserDto> usersTable;
    @FXML private TableColumn<UserDto, Long> idColumn;
    @FXML private TableColumn<UserDto, String> usernameColumn;
    @FXML private TableColumn<UserDto, String> emailColumn;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    @FXML private Label statusLabel;

    private final ApiClient apiClient = new ApiClient();
    private final ObservableList<UserDto> items = FXCollections.observableArrayList();

    private int page = 0;
    private int totalPages = 0;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        usersTable.setItems(items);

        sortFieldCombo.setItems(FXCollections.observableArrayList("username", "email", "id"));
        sortFieldCombo.getSelectionModel().select("username");

        sortDirCombo.setItems(FXCollections.observableArrayList("asc", "desc"));
        sortDirCombo.getSelectionModel().select("asc");

        pageSizeCombo.setItems(FXCollections.observableArrayList(10, 25, 50, 100));
        pageSizeCombo.getSelectionModel().select(Integer.valueOf(25));

        updatePagingControls();
        runSearch();
    }

    @FXML
    private void onSearch() {
        page = 0;
        runSearch();
    }

    @FXML
    private void onClear() {
        queryField.clear();
        sortFieldCombo.getSelectionModel().select("username");
        sortDirCombo.getSelectionModel().select("asc");
        pageSizeCombo.getSelectionModel().select(Integer.valueOf(25));
        page = 0;
        runSearch();
    }

    @FXML
    private void onPrev() {
        if (page > 0) {
            page--;
            runSearch();
        }
    }

    @FXML
    private void onNext() {
        if (page + 1 < totalPages) {
            page++;
            runSearch();
        }
    }

    private void setLoading(boolean loading) {
        searchButton.setDisable(loading);
        clearButton.setDisable(loading);
        prevButton.setDisable(loading || page <= 0);
        nextButton.setDisable(loading || page + 1 >= totalPages);
    }

    private void updatePagingControls() {
        String label = totalPages == 0 ? "Page 0 of 0" : "Page " + (page + 1) + " of " + totalPages;
        pageLabel.setText(label);
        prevButton.setDisable(page <= 0);
        nextButton.setDisable(page + 1 >= totalPages);
    }

    private void runSearch() {
        String q = queryField.getText();
        String sortField = sortFieldCombo.getValue();
        String sortDir = sortDirCombo.getValue();
        Integer size = pageSizeCombo.getValue();

        Task<PagedResponseDto<UserDto>> task = new Task<>() {
            @Override
            protected PagedResponseDto<UserDto> call() throws Exception {
                return apiClient.searchUsers(q, page, size, sortField, sortDir);
            }
        };

        setLoading(true);
        statusLabel.setText("Loading...");

        task.setOnSucceeded(evt -> {
            PagedResponseDto<UserDto> resp = task.getValue();
            List<UserDto> newItems = resp.getItems();

            items.setAll(newItems == null ? List.of() : newItems);
            totalPages = resp.getTotalPages();
            page = resp.getPage();
            updatePagingControls();

            String msg = "Showing " + items.size() + " users (total " + resp.getTotalItems() + ")";
            statusLabel.setText(msg);
            setLoading(false);
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            statusLabel.setText("Error: " + (ex == null ? "unknown error" : ex.getMessage()));
            setLoading(false);
        });

        Thread thread = new Thread(task, "users-search");
        thread.setDaemon(true);
        thread.start();
    }
}

