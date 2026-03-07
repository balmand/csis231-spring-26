package com.csis231.javafxclient.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class MainController {
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab employeesTab;
    @FXML
    private Tab departmentsTab;
    @FXML
    private Tab itemsTab;

    private EmployeeController employeeController;
    private DepartmentController departmentController;
    private ItemController itemController;

    @FXML
    public void initialize() {
        // Load employee tab content
        try {
            FXMLLoader employeeLoader = new FXMLLoader(getClass().getResource("/fxml/employee.fxml"));
            employeesTab.setContent(employeeLoader.load());
            employeeController = employeeLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Load department tab content
        try {
            FXMLLoader departmentLoader = new FXMLLoader(getClass().getResource("/fxml/department.fxml"));
            departmentsTab.setContent(departmentLoader.load());
            departmentController = departmentLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            FXMLLoader itemLoader = new FXMLLoader(getClass().getResource("/fxml/item.fxml"));
            itemsTab.setContent(itemLoader.load());
            itemController = itemLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
