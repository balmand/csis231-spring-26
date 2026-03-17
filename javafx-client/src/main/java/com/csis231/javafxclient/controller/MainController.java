package com.csis231.javafxclient.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {
    @FXML
    private BorderPane rootPane;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab employeesTab;
    @FXML
    private Tab departmentsTab;

    private EmployeeController employeeController;
    private DepartmentController departmentController;

    @FXML
    public void initialize() {
        loadMainTabs();
    }

    private void loadMainTabs() {
        try {
            FXMLLoader employeeLoader = new FXMLLoader(getClass().getResource("/fxml/employee.fxml"));
            employeesTab.setContent(employeeLoader.load());
            employeeController = employeeLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FXMLLoader departmentLoader = new FXMLLoader(getClass().getResource("/fxml/department.fxml"));
            departmentsTab.setContent(departmentLoader.load());
            departmentController = departmentLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
