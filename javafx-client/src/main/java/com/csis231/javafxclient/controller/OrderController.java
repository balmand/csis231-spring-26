package com.csis231.javafxclient.controller;

import com.csis231.javafxclient.model.OrderDto;
import com.csis231.javafxclient.service.ApiClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class OrderController {

    @FXML
    private TableView<OrderDto> orderTable;

    @FXML
    private TableColumn<OrderDto, Long> idColumn;

    @FXML
    private TableColumn<OrderDto, String> orderNumberColumn;

    @FXML
    private TableColumn<OrderDto, String> customerNameColumn;

    @FXML
    private TableColumn<OrderDto, Double> totalAmountColumn;

    @FXML
    private TextField orderNumberField;

    @FXML
    private TextField customerNameField;

    @FXML
    private TextField totalAmountField;

    @FXML
    private Label statusLabel;

    private final ApiClient apiClient = new ApiClient();
    private final ObservableList<OrderDto> orderList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        loadOrders();

        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, selectedOrder) -> {
            if (selectedOrder != null) {
                orderNumberField.setText(selectedOrder.getOrderNumber());
                customerNameField.setText(selectedOrder.getCustomerName());
                totalAmountField.setText(String.valueOf(selectedOrder.getTotalAmount()));
            }
        });
    }

    @FXML
    private void loadOrders() {
        try {
            List<OrderDto> orders = apiClient.getAllOrders();
            orderList.setAll(orders);
            orderTable.setItems(orderList);
            statusLabel.setText("Orders loaded successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error loading orders: " + e.getMessage());
        }
    }

    @FXML
    private void createOrder() {
        try {
            OrderDto order = new OrderDto();
            order.setOrderNumber(orderNumberField.getText());
            order.setCustomerName(customerNameField.getText());
            order.setTotalAmount(Double.parseDouble(totalAmountField.getText()));

            apiClient.createOrder(order);
            loadOrders();
            clearForm();
            statusLabel.setText("Order created successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error creating order: " + e.getMessage());
        }
    }

    @FXML
    private void updateOrder() {
        try {
            OrderDto selectedOrder = orderTable.getSelectionModel().getSelectedItem();
            if (selectedOrder == null) {
                statusLabel.setText("Select an order first.");
                return;
            }

            selectedOrder.setOrderNumber(orderNumberField.getText());
            selectedOrder.setCustomerName(customerNameField.getText());
            selectedOrder.setTotalAmount(Double.parseDouble(totalAmountField.getText()));

            apiClient.updateOrder(selectedOrder.getId(), selectedOrder);
            loadOrders();
            clearForm();
            statusLabel.setText("Order updated successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error updating order: " + e.getMessage());
        }
    }

    @FXML
    private void deleteOrder() {
        try {
            OrderDto selectedOrder = orderTable.getSelectionModel().getSelectedItem();
            if (selectedOrder == null) {
                statusLabel.setText("Select an order first.");
                return;
            }

            apiClient.deleteOrder(selectedOrder.getId());
            loadOrders();
            clearForm();
            statusLabel.setText("Order deleted successfully.");
        } catch (Exception e) {
            statusLabel.setText("Error deleting order: " + e.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        orderNumberField.clear();
        customerNameField.clear();
        totalAmountField.clear();
        orderTable.getSelectionModel().clearSelection();
    }
}