package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.ItemDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class ItemController {
    @FXML
    public TableView<ItemDto> itemTable;
    @FXML
    public TableColumn<ItemDto, Long> idColumn;
    @FXML
    public TableColumn<ItemDto, String> nameColumn;
    @FXML
    public TableColumn<ItemDto, Integer> serialNumberColumn;
    @FXML
    public TableColumn<ItemDto, String> categoryColumn;
    @FXML
    public TableColumn<ItemDto, String> statusColumn;

    @FXML
    public TextField nameField;
    @FXML
    public ComboBox<String> categoryComboBox;
    @FXML
    public TextField statusField;

    @FXML
    public Button createButton;
    @FXML
    public Button updateButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Button refreshButton;
    @FXML
    public Label statusLabel;

    private ApiClient apiClient;
    private ObservableList<ItemDto> itemList;

    public ItemController(){
        this.apiClient = new ApiClient();
        this.itemList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize(){
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        itemTable.setItems(itemList);

        ObservableList<String> categories = FXCollections.observableArrayList(
                "ELECTRONICS", "CLOTHING", "BOOKS"
        );
        categoryComboBox.setItems(categories);

        itemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadItemToForm(newSelection);
                updateButton.setDisable(false);
                deleteButton.setDisable(false);
            } else {
                clearForm();
                updateButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        refreshItem();
    }

    @FXML
    public void createItem() {
        try {
            ItemDto item = new ItemDto();
            item.setName(nameField.getText());
            item.setCategory(categoryComboBox.getValue());
            item.setStatus(statusField.getText());

            apiClient.createItem(item);
            statusLabel.setText("Item created successfully!");
            refreshItem();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void updateItem() {
        ItemDto selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select an item to update");
            return;
        }

        try{
            selected.setName(nameField.getText());
            selected.setStatus(statusField.getText());
            selected.setCategory(categoryComboBox.getValue());

            apiClient.updateItem(selected.getId(), selected);
            statusLabel.setText("Item updated successfully!");
            refreshItem();
            clearForm();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteItem() {
        ItemDto selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select an item to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Item");
        alert.setContentText("Are you sure you want to delete item: " + selected.getName() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                apiClient.deleteItem(selected.getId());
                statusLabel.setText("Item deleted successfully!");
                refreshItem();
                clearForm();
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void refreshItem() {
        try {
            List<ItemDto> items = apiClient.getAllItems();
            itemList.clear();
            itemList.addAll(items);
            statusLabel.setText("Items loaded: " + items.size());
        } catch (Exception e) {
            statusLabel.setText("Error loading departments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadItemToForm(ItemDto item) {
        nameField.setText(item.getName());
        categoryComboBox.setValue(item.getCategory());
        statusField.setText(item.getStatus());
    }

    private void clearForm() {
        nameField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        statusField.clear();
    }
}
